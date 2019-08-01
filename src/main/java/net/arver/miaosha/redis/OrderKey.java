package net.arver.miaosha.redis;

/**
 * 订单key.
 */
public class OrderKey extends BasePrefix {

    /**
     * 默认过期时间一天.
     */
    private static final int DEFALUT_EXPIRESECONDS = 24 * 60 * 60;

    public OrderKey(final String prefix, final int expireSeconds) {
        super(prefix, expireSeconds);
    }

    /**
     * 根据用户id商品id查询秒杀订单.
     */
    public static final OrderKey MIAOSHAORDER_BY_UID_GID = new OrderKey("moug", DEFALUT_EXPIRESECONDS);
}
