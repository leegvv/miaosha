package net.arver.miaosha.redis;

/**
 * 用户前缀
 */
public final class MiaoshaUserKey extends BasePrefix {

    private static final int TOKEN_EXPIRE = 3600 * 24 * 2;

    private MiaoshaUserKey(final String prefix, final int expireSeconds) {
        super(prefix, expireSeconds);
    }

    public static final MiaoshaUserKey TOKEN = new MiaoshaUserKey("tk", TOKEN_EXPIRE);

    public static final MiaoshaUserKey BY_ID = new MiaoshaUserKey("id", 0);
}
