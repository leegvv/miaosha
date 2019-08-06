package net.arver.miaosha.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import net.arver.miaosha.domain.MiaoshaOrder;
import net.arver.miaosha.domain.MiaoshaUser;
import net.arver.miaosha.service.GoodsService;
import net.arver.miaosha.service.MiaoshaService;
import net.arver.miaosha.service.OrderService;
import net.arver.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * MQ消费者.
 */
@Service
public class MQReceiver {

    /**
     * 日志.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MQReceiver.class);

    /**
     * rabbit模板.
     */
    @Autowired
    AmqpTemplate amqpTemplate;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receiveMiaoshaMessage(final String message) {
        LOGGER.info("receive message:" + message);
        final MiaoshaMessage miaoshaMessage = JSONObject.parseObject(message, MiaoshaMessage.class);
        final MiaoshaUser user = miaoshaMessage.getUser();
        final long goodsId = miaoshaMessage.getGoodsId();
        final GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        final Integer stockCount = goods.getStockCount();
        if (stockCount <= 0) {
            return;
        }
        // 判断是否已经秒杀到了
        final MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if (order != null) {
            return;
        }
        // 减库存 下订单 写入秒杀订单
        miaoshaService.miaosha(user, goods);


    }

    /**
     * 接受消息.
     * @param message 消息
     *//*
    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(final String message) {
        LOGGER.info("receive message:" + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(final String message) {
        LOGGER.info("topic queue1 message:" + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(final String message) {
        LOGGER.info("topic queue2 message:" + message);
    }

    @RabbitListener(queues = MQConfig.HEADER_QUEUE)
    public void receiveHeaderQueue(final byte[] message) {
        LOGGER.info("header queue message:" + new String(message));
    }*/


}
