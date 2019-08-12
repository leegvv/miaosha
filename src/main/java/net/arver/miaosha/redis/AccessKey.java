package net.arver.miaosha.redis;

public class AccessKey extends BasePrefix{

    private AccessKey(final String prefix, final int expireSeconds) {
        super(prefix, expireSeconds);
    }

    public static AccessKey withExpire(int expireSeconds) {
        return new AccessKey("access", expireSeconds);
    }
}
