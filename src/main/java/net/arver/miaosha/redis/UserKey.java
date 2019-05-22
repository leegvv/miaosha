package net.arver.miaosha.redis;

/**
 * 用户前缀
 */
public final class UserKey extends BasePrefix {
    private UserKey(final String prefix) {
        super(prefix);
    }

    public static final UserKey BY_ID = new UserKey("id");
    public static final UserKey BY_NAME = new UserKey("name");
}
