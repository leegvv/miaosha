package net.arver.miaosha.redis;

/**
 * 商品key.
 */
public class GoodsKey extends BasePrefix {
    public GoodsKey(final String prefix, final int expireSeconds) {
        super(prefix, expireSeconds);
    }

    public static final GoodsKey GOODS_LIST = new GoodsKey("gl", 60);

    public static final GoodsKey GOODS_DETAIL = new GoodsKey("gd", 60);

    public static final GoodsKey MIAOSHA_GOODSSTOCK = new GoodsKey("gs", 0);
}
