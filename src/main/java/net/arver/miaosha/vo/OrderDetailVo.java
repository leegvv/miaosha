package net.arver.miaosha.vo;

import net.arver.miaosha.domain.OrderInfo;

public class OrderDetailVo {

    private GoodsVo goods;

    private OrderInfo order;

    public GoodsVo getGoods() {
        return goods;
    }

    public void setGoods(final GoodsVo goods) {
        this.goods = goods;
    }

    public OrderInfo getOrder() {
        return order;
    }

    public void setOrder(final OrderInfo order) {
        this.order = order;
    }
}
