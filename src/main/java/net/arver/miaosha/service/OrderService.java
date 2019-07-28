package net.arver.miaosha.service;

import net.arver.miaosha.dao.OrderDao;
import net.arver.miaosha.domain.MiaoshaOrder;
import net.arver.miaosha.domain.MiaoshaUser;
import net.arver.miaosha.domain.OrderInfo;
import net.arver.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 订单服务.
 */
@Service
public class OrderService {

    /**
     * 订单服务.
     */
    @Autowired
    OrderDao orderDao;

    /**
     * 根据用户id和商品id查询订单信息.
     * @param userId 用户id
     * @param goodsId 商品id
     * @return 秒杀订单
     */
    public MiaoshaOrder getMiaoshaOrderByUserIdAndGoodsId(final long userId, final long goodsId) {
        return orderDao.getMiaoshaOrderByUserIdAndGoodsId(userId, goodsId);
    }

    /**
     * 创建订单.
     * @param user 用户信息
     * @param goods 商品
     * @return 订单信息
     */
    @Transactional
    public OrderInfo createOrder(final MiaoshaUser user, final GoodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        long orderId = orderDao.insert(orderInfo);
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goods.getId());
        miaoshaOrder.setOrderId(orderId);
        miaoshaOrder.setUserId(user.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);
        return orderInfo;
    }


}