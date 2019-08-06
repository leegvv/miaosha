package net.arver.miaosha.service;

import net.arver.miaosha.domain.MiaoshaOrder;
import net.arver.miaosha.domain.MiaoshaUser;
import net.arver.miaosha.domain.OrderInfo;
import net.arver.miaosha.redis.MiaoshaKey;
import net.arver.miaosha.redis.RedisService;
import net.arver.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 秒杀服务.
 */
@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    /**
     * 秒杀.
     * @param user 用户信息
     * @param goods 商品vo
     * @return 订单信息
     */
    public OrderInfo miaosha(final MiaoshaUser user, final GoodsVo goods) {
        //减库存、下订单、写入秒杀订单
        final boolean success = goodsService.reduceStock(goods);
        if (success) {
            return orderService.createOrder(user, goods);
        }
        setGoodsOver(goods.getId());
        return null;
    }

    /**
     * 获取秒杀结果.
     * @param userId 用户id
     * @param goodsId 货物id
     * @return 结果
     */
    public long getMiaoshaResult(final Long userId, final long goodsId) {
        final MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdAndGoodsId(userId, goodsId);
        if (order != null) {
            return order.getOrderId();
        } else {
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    /**
     * 查询秒杀是否结束.
     * @param goodsId 货物id
     * @return 是否结束
     */
    private boolean getGoodsOver(final long goodsId) {
        return redisService.exist(MiaoshaKey.GOODS_OVER, "" + goodsId);
    }

    /**
     * 设置秒杀结束.
     * @param goodsId 货物id
     */
    private void setGoodsOver(final long goodsId) {
        redisService.set(MiaoshaKey.GOODS_OVER, "" + goodsId, true);
    }
}
