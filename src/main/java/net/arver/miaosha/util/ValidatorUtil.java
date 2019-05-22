package net.arver.miaosha.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验工具类.
 */
public final class ValidatorUtil {

    /**
     * 隐藏构造函数.
     */
    private ValidatorUtil() {

    }

    private static final Pattern MOBILE_PATTERN = Pattern.compile("1\\d{10}");

    /**
     * 判断是不是手机号.
     * @param src 输入
     * @return 是否是手机号
     */
    public static boolean isMobile(final String src) {
        if (StringUtils.isBlank(src)) {
            return false;
        }
        final Matcher matcher = MOBILE_PATTERN.matcher(src);
        return matcher.matches();
    }
}
