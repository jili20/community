package com.nowcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/** Kafka - 事件消费者 - 被动触发
 * @author bing  @create 2020/5/15 3:30 下午
 */
@Component
public class EventConsumer implements CommunityConstant {
    // 声明记录日志的组件
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private ElasticsearchService elasticsearchService;

    @Value("${wk.image.command}")
    private String wkImageCommand;

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.share.name}")
    private String shareBuckertName;

    @Autowired // 可执行定时任务的线程池
    private ThreadPoolTaskScheduler taskScheduler;

    //消费三种主题
    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record){
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空！");
            return;
        }
        // 把发送过来的json格式的字符串，转回对象格式
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误！");
            return;
        }

        // 发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);// 消息发布者： 1号系统用户
        message.setToId(event.getEntityUserId());//消息接收者：作者
        message.setConversationId(event.getTopic());//这里存主题类型
        message.setCreateTime(new Date()); // 发送时间

        Map<String, Object> content = new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());

        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }
        // 将对象转json字符串
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);// 存数据
    }

    // 消费发帖事件
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record){
        if (record == null || record.value() == null){
            logger.error("消息内容为空");
            return ;
        }

        //将消息转换为event对象
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null){
            logger.error("消息格式有误");
            return;
        }

        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticsearchService.saveDiscussPost(post);
    }


    // 消费删帖事件
    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDeleteMessage(ConsumerRecord record){
        if (record == null || record.value() == null){
            logger.error("消息内容为空");
            return ;
        }

        //将消息转换为event对象
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null){
            logger.error("消息格式有误");
            return;
        }
        elasticsearchService.deldteDiscussPost(event.getEntityId());
    }

    // 消费分享事件
    @KafkaListener(topics = TOPIC_SHARE)
    public void handleShareMessage(ConsumerRecord record){
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空！");
            return;
        }
        // 把发送过来的json格式的字符串，转回对象格式
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误！");
            return;
        }

        String htmlUrl = (String) event.getData().get("htmlUrl");
        String fileName = (String) event.getData().get("fileName");
        String suffix = (String) event.getData().get("suffix");

        String cmd = wkImageCommand + " --quality 75 "
                + htmlUrl + " " + wkImageStorage + "/" + fileName + suffix;

        try {
            Runtime.getRuntime().exec(cmd);
            logger.info("生成长图成功：" + cmd);
        } catch (IOException e) {
            logger.error("生成长图失败：" + e.getMessage());
        }
        // 启用定时器，监视该图片，一旦生成了，则上传至七牛云
        UploadTask task = new UploadTask(fileName, suffix);
        Future future = taskScheduler.scheduleAtFixedRate(task, 500);//每隔500毫秒执行一遍
        task.setFuture(future);
    }

    class UploadTask implements Runnable{
        //文件名
        private String fileName;
        //文件后缀
        private String suffix;
        //启动任务的返回值，可以用来停止定时器
        private Future future;
        //开始时间
        private long startTime;
        //上传次数
        private int uploadTimes;

        public UploadTask(String fileName, String suffix) {
            this.fileName = fileName;
            this.suffix = suffix;
            this.startTime = System.currentTimeMillis();
        }

        public void setFuture(Future future) {
            this.future = future;
        }

        @Override
        public void run() {
            //生成失败
            if (System.currentTimeMillis() - startTime > 30000){
                logger.error("执行时间过长，终止任务："+fileName);
                future.cancel(true);
                return ;
            }
            // 上传失败
            if (uploadTimes >= 3){
                logger.error("上传次数过多，终止任务：" + fileName);
                future.cancel(true);
                return;
            }

            //本地存放文件的路径
            String path = wkImageStorage + "/" + fileName + suffix;
            File file = new File(path);
            if (file.exists()){
                logger.info(String.format("开始第%d次上传[%s]",++uploadTimes,fileName));
                //设置响应信息
                StringMap policy  = new StringMap();
                policy.put("returnBody", CommunityUtil.getJSONString(0));
                //生成上传凭证
                Auth auth = Auth.create(accessKey,secretKey);
                String uploadToken = auth.uploadToken(shareBuckertName,fileName,3600,policy);
                //指定上传机房
                UploadManager manager = new UploadManager(new Configuration(Zone.huanan()));//上传到华南机房
                try {
                    //开始上传图片
                    Response response = manager.put(path,fileName,uploadToken,null,"image/" + suffix,false);
                    //处理响应结果
                    JSONObject json = JSONObject.parseObject(response.bodyString());
                    if (json == null || json.get("code") == null || !json.get("code").toString().equals("0")){
                        logger.info(String.format("第%d次上传失败[%s]",uploadTimes,fileName));
                    }else {
                        logger.info(String.format("第%d次上传成功[%s]",uploadTimes,fileName));
                        future.cancel(true);//上传成功，任务取消
                    }
                }catch (QiniuException e){
                    logger.info(String.format("第%d次上传失败[%s]",uploadTimes,fileName));
                }
            }else {
                logger.info("等待图片生成[" + fileName + "].");
            }
        }
    }
}
