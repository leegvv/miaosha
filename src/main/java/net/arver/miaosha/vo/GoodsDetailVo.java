package net.arver.miaosha.vo;

import net.arver.miaosha.domain.MiaoshaUser;

/**
 * 货物详情vo.
 */
public class GoodsDetailVo {
    private int miaoshaStatus;

    private long remainSeconds;

    private GoodsVo goods;

    private MiaoshaUser user;

    public int getMiaoshaStatus() {
        return miaoshaStatus;
    }

    public void setMiaoshaStatus(final int miaoshaStatus) {
        this.miaoshaStatus = miaoshaStatus;
    }

    public long getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(final long remainSeconds) {
        this.remainSeconds = remainSeconds;
    }

    public GoodsVo getGoods() {
        return goods;
    }

    public void setGoods(final GoodsVo goods) {
        this.goods = goods;
    }

    public MiaoshaUser getUser() {
        return user;
    }

    public void setUser(final MiaoshaUser user) {
        this.user = user;
    }
}
