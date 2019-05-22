package net.arver.miaosha.redis;

/**
 * redis中key前缀
 */
public interface KeyPrefix {

    /**
     * 过期时间
     * @return 过期时间
     */
    int expireSeconds();

    /**
     * 获取前缀
     * @return 前缀
     */
    String getPrefix();
}
