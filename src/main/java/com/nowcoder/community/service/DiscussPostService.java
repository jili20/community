package com.nowcoder.community.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.util.SensitiveFilter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author bing  @create 2020/5/7 11:11 上午
 */
@Service
public class DiscussPostService {

    private static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;// 过滤敏感词工具

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    // Caffeine 核心接口：Cache, LoadingCache 数据取好再返回, AsyncLoadingCache 异步，支持并发

    // 帖子列表缓存（ key,value)
    private LoadingCache<String, List<DiscussPost>> postListCache;

    // 帖子总数缓存
    private LoadingCache<Integer, Integer> postRowsCache;

    @PostConstruct
    public void init(){
        // 初始化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Nullable
                    @Override
                    public List<DiscussPost> load(@NonNull String key) throws Exception {//查询数据库得到 初始化数据的办法
                        if (key == null || key.length() == 0) {
                            throw  new IllegalArgumentException("参数错误！");
                        }
                        String[] params = key.split(":"); // 切割
                        if (params == null || params.length != 2) {
                            throw  new IllegalArgumentException("参数错误！");
                        }
                        // 转型
                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);
                        // 这里可以加二级缓存： Redis -> mysql
                        logger.debug("load post list from DB");
                        return discussPostMapper.selectDiscussPosts(0, offset, limit, 1);// 1 热帖
                    }
                });

        // 初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer key) throws Exception {
                        logger.debug("load post list from DB");
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });
    }

    // 查询某页数据
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderMode){
        if (userId == 0 && orderMode == 1) { // 帖子首页时 userId=0
            return postListCache.get(offset + ":" + limit); // 缓存
        }
        logger.debug("load post list from DB");
        return discussPostMapper.selectDiscussPosts(userId, offset, limit, orderMode);
    }

    // 查询行数
    public int findDiscussPostRows(int userId){
        if (userId == 0) {
            return postRowsCache.get(userId); // 缓存
        }
        logger.debug("load post list from DB");
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    // 添加帖子
    public int addDiscussPost(DiscussPost post) {
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        //转义HTML标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        //过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    //查询，帖子详情
    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }

    // 更新评论数量
    public int updateCommentCount(int id, int commentCount){
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    // 根据id修改类型
    public int updateType(int id, int type){
        return discussPostMapper.updateType(id, type);
    }

    /**
     * 修改帖子的状态
     * @param id
     * @param status 加精、删除
     * @return
     */
    public int updateStatus(int id, int status){
        return discussPostMapper.updateStatus(id, status);
    }

    /**
     * 修改帖子的分数
     * @param id
     * @param score
     * @return
     */
    public int updateScore(int id, double score){
        return discussPostMapper.updateScore(id, score);
    }
}
