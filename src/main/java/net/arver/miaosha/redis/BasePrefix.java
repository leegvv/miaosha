package net.arver.miaosha.redis;

/**
 * redis前缀抽象实现
 */
public abstract class BasePrefix implements KeyPrefix {

    private String prefix;

    private int expireSeconds;

    public BasePrefix(final String prefix) {
        this.prefix = prefix;
    }

    public BasePrefix(final String prefix, final int expireSeconds) {
        this.prefix = prefix;
        this.expireSeconds = expireSeconds;
    }

    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        final String className = getClass().getSimpleName();
        return className + ":" + prefix;
    }
}
