package net.arver.miaosha.redis;

public class MiaoshaKey extends BasePrefix {
    private MiaoshaKey(final String prefix) {
        super(prefix);
    }

    public static final MiaoshaKey GOODS_OVER = new MiaoshaKey("go");
}
