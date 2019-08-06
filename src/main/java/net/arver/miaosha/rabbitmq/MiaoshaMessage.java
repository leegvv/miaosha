package net.arver.miaosha.rabbitmq;

import net.arver.miaosha.domain.MiaoshaUser;

public class MiaoshaMessage {
    public MiaoshaUser user;
    public long goodsId;

    public MiaoshaUser getUser() {
        return user;
    }

    public void setUser(final MiaoshaUser user) {
        this.user = user;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(final long goodsId) {
        this.goodsId = goodsId;
    }
}
