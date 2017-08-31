package com.lpan.jms;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by Administrator on 2017/7/25.
 */
public class JMSProducer {

    //默认连接用户名
    private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;
    //默认连接密码
    private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
    //默认连接地址
    private static final String BROKEURL = ActiveMQConnection.DEFAULT_BROKER_URL;
    //发送的消息数量
    private static final int SENDNUM = 10;

    public static void main(String[] args) {
        //初始一个连接工厂
        ConnectionFactory connectionFactory;
        //初始连接
        Connection connection = null;
        //初始session会话，接受或发送消息的线程
        Session session;
        //初始消息目的地
        Destination destination;
        //初始消息生产者
        MessageProducer messageProducer;
        //实例化连接工厂
        connectionFactory = new ActiveMQConnectionFactory(USERNAME, PASSWORD, BROKEURL);
        try {
            //通过工厂创建连接
            connection = connectionFactory.createConnection();
            //启动连接
            connection.start();
            //通过连接创建session
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            //创建一个名为hello的消息队列
            destination = session.createQueue("Hello");
            //创建消息生产者
            messageProducer = session.createProducer(destination);
            //发送消息
            sendMessage(session,messageProducer);
            session.commit();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null){
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void sendMessage(Session session,MessageProducer messageProducer) throws Exception{
        for (int i = 0; i < JMSProducer.SENDNUM; i++) {
            //创建一条文本消息
            TextMessage message = session.createTextMessage("ActiveMQ"+i+"发送消息" +i);
            System.out.println("发送消息：ActiveMQ"+i+"发送消息" +i);
            //通过消息生产者发出消息
            messageProducer.send(message);
        }

    }
}
