package com.pzl.program.frametool.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.UUID;

/**
 * SpringBoot 整合 ActiveMQ
 * <p>
 * 传统的java调用方式在代码上十分冗余，大量的重复代码。
 * 基于 SpringBoot 注解的方式整合ActiveMQ将会变得十分简单,JmsMessagingTemplate注入。
 *
 * @author pzl
 * @date 2020-04-05
 */
@Slf4j
@Component
public class ActiveMq2SpringBoot {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    /**
     * 点对点模式:生产者发送消息到MQ
     */
    // @Scheduled(fixedDelay = 2000)
    public void produceQueue() {
        String message = UUID.randomUUID().toString().substring(0, 8);
        //定时向MQ发送消息
        jmsMessagingTemplate.convertAndSend(new ActiveMQQueue("queueName"), message);
        System.out.println("点对点模式生产者发送消息message={}" + message);
    }

    /**
     * 点对点模式:消费者接收消息
     *
     * @param msg 消息
     */
    //@JmsListener(destination = "queueName")
    public void receiveQueue(String msg) {
        System.out.println("点对点模式消费者从队列收到消息---" + msg);
    }

    /***
     * 订阅模式：生产者发送消息
     */
    //@Scheduled(fixedDelay = 2000)
    public void produceTopic() {
        String message = "发布订阅-" + UUID.randomUUID().toString().substring(0, 6);
        jmsMessagingTemplate.convertAndSend(new ActiveMQTopic("topicName"), message);
        System.out.println("生产者发布订阅完成message=" + message);
    }

    /***
     * 订阅模式：消费者接收消息
     */
    //@JmsListener(destination = "topicName")
    public void receive(TextMessage text) throws JMSException {
        try {
            System.out.println("消费者收到订阅的主题-" + text.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}