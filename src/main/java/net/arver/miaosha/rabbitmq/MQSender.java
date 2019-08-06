package net.arver.miaosha.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * MQ生产者.
 */
@Service
public class MQSender {

    /**
     * 日志.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MQSender.class);

    /**
     * amqp模板服务.
     */
    @Autowired
    AmqpTemplate amqpTemplate;

    public void sendMiaoshaMessage(final MiaoshaMessage message) {
        final String msg = JSONObject.toJSONString(message);
        LOGGER.info("send message:" + msg);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE, msg);
    }

   /* *//**
     * 发送消息.
     * @param message 消息
     *//*
    public void send(final Object message) {
        final String msg = JSONObject.toJSONString(message);
        LOGGER.info("send message:" + msg);
        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
    }

    public void sendTopic(final Object message) {
        final String msg = JSONObject.toJSONString(message);
        LOGGER.info("send topic message:" + msg);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key1", msg + 1);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key2", msg + 2);
    }

    public void sendFanout(final Object message) {
        final String msg = JSONObject.toJSONString(message);
        LOGGER.info("send fanout message:" + msg);
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "", msg);
    }

    public void sendHeader(final Object message) {
        final String msg = JSONObject.toJSONString(message);
        LOGGER.info("send header message:" + msg);
        final MessageProperties properties = new MessageProperties();
        properties.setHeader("header1", "value1");
        properties.setHeader("header2", "value2");
        final Message obj = new Message(msg.getBytes(), properties);
        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE, "", obj);
    }*/
}
