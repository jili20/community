package com.nowcoder.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author bing  @create 2020/5/15 2:16 下午
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTest {

    @Autowired
    private KafkaProducer kafkaProducer;

   @Test
   public void testKafka(){
       kafkaProducer.sendMessage("test","你好");
       kafkaProducer.sendMessage("test","在吗");

       try {
           Thread.sleep(1000*3);
       } catch (InterruptedException e) {
           e.printStackTrace();
       }
   }
}

@Component
class KafkaProducer{ //生产者

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendMessage(String topic,String content){
        kafkaTemplate.send(topic, content);
    }

}

@Component
class KafkaConsumer{ // 消费者

    @KafkaListener(topics = {"test"}) // 监听test主题
    public void handleMessage(ConsumerRecord record){
        System.out.println(record.value());
    }
}