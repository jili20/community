package com.nowcoder.community.entity;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/** Kafka - 事件消费者 - 被动触发
 * @author bing  @create 2020/5/15 3:30 下午
 */
@Component
public class EventConsumer implements CommunityConstant {
    // 声明记录日志的组件
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

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
        message.setToId(event.getEntityId());//消息接收者：作者
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
}
















