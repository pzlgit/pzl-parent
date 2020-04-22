package com.pzl.program.frametool.mq;

import com.pzl.program.toolkit.config.GlobalConfig;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * ActiveMQ 消息中间件
 * <p>
 * 好处：
 * 异步、解耦、削峰
 * ActiveMQ：万级吞吐量
 * kafka：十万吞吐量（一般小公司达不到这个级别）
 * <p>
 * 一、点对点消息传递域的特点
 * （1）每个消息只能有一个消费者，类似1对1的关系。好比个人快递只领取自己的。
 * （2）消息的 生产者 和 消费者 之间没有时间上的相关性。无论消费者在生产者发送消息的时候是否处于运行状态，
 * 消费者都可以提取消息。好比我们的发送短信，发送者发送后不见得接收者会即收即看。
 * （3）消息被消费后队列中不会再存储，所以消费者不会消费到已经被消费掉的消息。
 * <p>
 * 二、发布/订阅消息传递域的特点 （先启动订阅再启动生产，不然消息丢失）
 * （1）生产者 将消息发布到topic中，每个消息可以有多个消费者，属于1：N的关系
 * （2）生产者 和 消费者 之间有时间上的相关性。订阅某一个主题的消费者只能消费自它订阅之后发布的消息。
 * （3）生产者生产时，topic不保存消息它是无状态的不落地，假如无人订阅就去生产，那就是一条废消息，所以，一般先启动消费者再启动生产者。
 * <p>
 * 三、开发步骤
 * 1：创建一个connection factory
 * 2：通过connection factory来创建JMS connection
 * 3：启动JMS connection
 * 4：通过connection创建JMS session
 * 5：创建JMS destination
 * 6：创建JMS producer或者创建JMS message，并设置destination
 * 7：创建JMS consumer或者是注册一个JMS message listener
 * 8：发送或者接受JMS message(s)
 * 9：关闭所有的JMS资源(connection, session, producer, consumer等)
 *
 * @author pzl
 * @date 2020-04-05
 */
public class ActiveMqUtil {

    //连接地址
    public static final String ACTIVEMQ_URL = "tcp://" + GlobalConfig.IP + ":61616";
    //队列名称
    public static final String QUEUE_NAME = "Queue";

    public static final String TOPIC_NAME = "TOPIC";

    /**
     * 生产者向消息队列存入消息(原生方式)(点对点模式)
     */
    public void queueProducer() throws JMSException {
        //1 创建连接工场,使用默认用户名密码，编码不再体现
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        //2 获得连接并启动
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        //3 创建会话,此步骤有两个参数，第一个是否以事务的方式提交，第二个默认的签收方式
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //4 创建队列
        Queue queue = session.createQueue(QUEUE_NAME);
        //5 创建生产者
        MessageProducer messageProducer = session.createProducer(queue);
        //生产6条消息给MQ
        for (int i = 1; i <= 6; i++) {
            //6 创建消息
            TextMessage message = session.createTextMessage("--message--" + i);
            //7 通过消息生产者发布消息
            messageProducer.send(message);
        }
        //8 关闭资源
        messageProducer.close();
        session.close();
        connection.close();
        System.out.println("------------消息发送到MQ完毕-----------");
    }

    /**
     * 消费者(原生方式)(点对点模式)
     */
    public void queueConsumer() throws Exception {
        //1 创建连接工场,使用默认用户名密码，编码不再体现
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        //2 获得连接并启动
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        //3 创建会话,此步骤有两个参数，第一个是否以事务的方式提交，第二个默认的签收方式
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //4 创建队列
        Queue queue = session.createQueue(QUEUE_NAME);
        //5 创建消费者
        MessageConsumer messageConsumer = session.createConsumer(queue);
        //6 监听器 监听MQ 用来接收消息
        messageConsumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                TextMessage textMessage = (TextMessage) message;
                try {
                    System.out.println("消费者收到消息:" + textMessage.getText());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
        System.in.read();
        messageConsumer.close();
        session.close();
        connection.close();
    }

    /**
     * 生产者向消息队列存入消息(原生方式)(发布订阅模式)
     */
    public void topicProducer() throws JMSException {
        //1 创建连接工场,使用默认用户名密码，编码不再体现
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        //2 获得连接并启动
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        //3 创建会话,此步骤有两个参数，第一个是否以事务的方式提交，第二个默认的签收方式
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(TOPIC_NAME);
        //4 创建主题生产者
        MessageProducer messageProducer = session.createProducer(topic);
        //5 向MQ发送消息
        for (int i = 1; i <= 3; i++) {
            TextMessage textMessage = session.createTextMessage("message-topic" + i);
            messageProducer.send(textMessage);
        }
        messageProducer.close();
        session.close();
        connection.close();
        System.out.println("----------------主题消息发送到MQ完成----------------");
    }

    /**
     * 消费者(原生方式)(发布订阅模式)
     */
    public void topicConsumer() throws Exception {
        //1 创建连接工场,使用默认用户名密码，编码不再体现
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        //2 获得连接并启动
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        //3 创建会话,此步骤有两个参数，第一个是否以事务的方式提交，第二个默认的签收方式
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //4 创建主题
        Topic topic = session.createTopic(TOPIC_NAME);
        // 接收消息
        MessageConsumer messageConsumer = session.createConsumer(topic);
        messageConsumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if (message instanceof TextMessage) {
                    TextMessage msg = (TextMessage) message;
                    try {
                        System.out.println("***消费者获得主题:" + msg.getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        System.in.read();
        messageConsumer.close();
        session.close();
        connection.close();
    }

}
