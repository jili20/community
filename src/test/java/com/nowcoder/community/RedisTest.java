package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.concurrent.TimeUnit;
/** redis 的基本操作
 * @author bing  @create 2020/5/13 12:45 下午
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test //字符串
    public void testString()
    {
        String redisKey = "test:count";
        redisTemplate.opsForValue().set(redisKey,1);//存数据
        System.out.println(redisTemplate.opsForValue().get(redisKey));//取数据
        System.out.println(redisTemplate.opsForValue().increment(redisKey));//增加数据
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));//减少数据

    }

    @Test // 哈希
    public void testHashes()
    {
        String redisKey = "test:user";
        redisTemplate.opsForHash().put(redisKey,"id", 1);
        redisTemplate.opsForHash().put(redisKey,"username", "zhangsan");
        System.out.println(redisTemplate.opsForHash().get(redisKey,"id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey,"username"));

    }

    @Test // 列表
    public void testList()
    {
        String redisKey = "/test:ids";
        redisTemplate.opsForList().leftPush(redisKey,101);//存数据
        redisTemplate.opsForList().leftPush(redisKey,102);
        redisTemplate.opsForList().leftPush(redisKey,103);

        System.out.println(redisTemplate.opsForList().size(redisKey));//获取当前列表数据总数
        System.out.println(redisTemplate.opsForList().index(redisKey,0)); // 获取索引位置为0的数据
        System.out.println(redisTemplate.opsForList().range(redisKey,0,2)); //获取索引0到2范围的数据

        System.out.println(redisTemplate.opsForList().leftPop(redisKey));//从左边弹出一个数据
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));

    }

    @Test // 无序集合
    public void testSets()
    {
        String redisKey = "test:teachers";
        redisTemplate.opsForSet().add(redisKey,"刘备","关羽","张飞","赵云","诸葛亮");

        System.out.println(redisTemplate.opsForSet().size(redisKey)); // 查询数据总数：5
        System.out.println(redisTemplate.opsForSet().pop(redisKey)); // 随机弹出一个数据：关羽
        System.out.println(redisTemplate.opsForSet().members(redisKey));// 统计数量明细 [赵云, 刘备, 张飞, 诸葛亮]

    }
    
    @Test // 有序集合
    public void testSortedSets()
    {
        String redisKey = "test:students";

        redisTemplate.opsForZSet().add(redisKey,"唐憎",80);//存数据
        redisTemplate.opsForZSet().add(redisKey,"悟空",90);//存数据
        redisTemplate.opsForZSet().add(redisKey,"八戒",50);//存数据
        redisTemplate.opsForZSet().add(redisKey,"沙憎",70);//存数据
        redisTemplate.opsForZSet().add(redisKey,"白龙马",60);//存数据

        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));// 统计总数：5
        System.out.println(redisTemplate.opsForZSet().score(redisKey,"八戒"));// 查询八戒的分数：50.0
        System.out.println(redisTemplate.opsForZSet().rank(redisKey,"八戒"));// 统计八戒分数排名，默认从小到大排：0
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey,"八戒"));// 统计八戒分数排名，从大到小排：4
        System.out.println(redisTemplate.opsForZSet().range(redisKey,0,2));// 取索引0到2范围内的数据，由小到大前三名，：[八戒, 白龙马, 沙憎]
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey,0,2));// 取索引0到2范围内的数据，由大到小前三名：[悟空, 唐憎, 沙憎]

    }

    @Test // 全局命令
    public void testKeys()
    {
        redisTemplate.delete("test:user"); // 删除一个key

        System.out.println(redisTemplate.hasKey("test:user")); // 判断key : test:user 是否存在
        redisTemplate.expire("test:students",5, TimeUnit.SECONDS); // 设置指定key 5秒过期，消失。
    }

    //多次访问同一个key ;绑定操作
    @Test
    public void testBoundOperations()
    {
        String redisKey = "test:count";//绑定字符串的 key
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey); // 绑定，类型可自定义
        operations.increment(); //绑定后直接调用 + 1 方法，无须再传redisKey
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }
    
    // 编程式事务
    @Test
    public void testTransactional()
    {
        Object obj = redisTemplate.execute(new SessionCallback() { //传入接口的实例
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "test:tx";
                operations.multi();//启动事务

                operations.opsForSet().add(redisKey,"zhangsan");
                operations.opsForSet().add(redisKey,"lisi");
                operations.opsForSet().add(redisKey,"wangwu");

                System.out.println(operations.opsForSet().members(redisKey)); // 事务中间查询无效，空[]

                return operations.exec();//提交事务：[1, 1, 1, [zhangsan, lisi, wangwu]]
            }
        });
        System.out.println(obj);
    }

    // HyperLogLog 的使用
    // 统计20万个重复数据的独立总数
    @Test
    public void testHyperLogLog(){
        String redisKey = "test:h11:01";

        for (int i = 0; i < 100000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey,i);
        }

        for (int i = 0; i < 100000; i++) {
            int r = (int) (Math.random()*100000 + 1);
            redisTemplate.opsForHyperLogLog().add(redisKey,r);
        }
        long size = redisTemplate.opsForHyperLogLog().size(redisKey);
        System.out.println(size); // 99565 
    }
    
    // 将三组数据合并，再统计合并后的重复数据的独立总数。
    @Test
    public void testHyperLogLogUnion(){
        String redisKey2 = "test:h11:02";
        for (int i = 0; i < 10000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey2,i);
        }

        String redisKey3 = "test:h11:03";
        for (int i = 5001; i < 15000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey3,i);
        }

        String redisKey4 = "test:h11:04";
        for (int i = 10001; i < 20000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey4,i);
        }

        String unionKey = "test:h11:union";
        redisTemplate.opsForHyperLogLog().union(unionKey,redisKey2,redisKey3,redisKey4);

        long size = redisTemplate.opsForHyperLogLog().size(unionKey);
        System.out.println(size); // 19891
    }
    
    // 统计一级数据的布尔值
    @Test
    public void testBitMap(){
        String redisKey = "test:bm:01";
        // 记录
        redisTemplate.opsForValue().setBit(redisKey,1,true);
        redisTemplate.opsForValue().setBit(redisKey,4,true);
        redisTemplate.opsForValue().setBit(redisKey,7,true);
        // 查询
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,2));
        // 统计
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(obj);
        
    }

    // 统计3组数据的布尔值，并对这3组数据做OR运算。
    @Test
    public void testBitMapOperation(){
        String redisKey2 = "test:bm:02";
        redisTemplate.opsForValue().setBit(redisKey2,0,true);
        redisTemplate.opsForValue().setBit(redisKey2,1,true);
        redisTemplate.opsForValue().setBit(redisKey2,2,true);

        String redisKey3 = "test:bm:03";
        redisTemplate.opsForValue().setBit(redisKey3,2,true);
        redisTemplate.opsForValue().setBit(redisKey3,3,true);
        redisTemplate.opsForValue().setBit(redisKey3,4,true);

        String redisKey4 = "test:bm:04";
        redisTemplate.opsForValue().setBit(redisKey4,4,true);
        redisTemplate.opsForValue().setBit(redisKey4,5,true);
        redisTemplate.opsForValue().setBit(redisKey4,6,true);

        String redisKey = "test:bm:or";
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(),redisKey2.getBytes(),redisKey3.getBytes(),redisKey4.getBytes());
                return connection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(obj);
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,2));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,3));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,4));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,5));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,6));
//        7
//        true
//        true
//        true
//        true
//        true
//        true
//        true
    }
}