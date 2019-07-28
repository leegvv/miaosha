package net.arver.miaosha.util;

import java.util.UUID;

/**
 * UUID工具类.
 */
public final class UUIDUtil {

    /**
     * 构造函数.
     */
    private UUIDUtil() { }

    /**
     * 生成uuid.
     * @return uuid
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
