package net.arver.miaosha.redis;

public class MiaoshaKey extends BasePrefix {
    private MiaoshaKey(final String prefix, final int expireSeconds) {
        super(prefix, expireSeconds);
    }

    public static final MiaoshaKey GOODS_OVER = new MiaoshaKey("go", 0);
    public static final MiaoshaKey MIAOSHA_PATH = new MiaoshaKey("mp", 60);
    public static final MiaoshaKey MIAOSHA_VERIFYCODE = new MiaoshaKey("vc", 300);
}
