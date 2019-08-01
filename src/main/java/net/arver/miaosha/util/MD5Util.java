package net.arver.miaosha.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.Assert;

/**
 * MD5工具类.
 */
public final class MD5Util {

    /**
     * 隐藏构造函数.
     */
    private MD5Util() {

    }

    /**
     * 盐.
     */
    private static final String SALT = "1a2b3c4d";

    /**
     * md5.
     *
     * @param src 源字符串
     * @return 加密后的字符串
     */
    public static String md5(final String src) {
        return DigestUtils.md2Hex(src);
    }

    /**
     * 表单密码加密.
     *
     * @param formPass 表单密码
     * @param salt     盐
     * @return 加密后的密码
     */
    public static String formPassToDBPass(final String formPass, final String salt) {
        Assert.isTrue(salt != null && salt.length() > 5, "盐长度不小于5");
        String str = "" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    /**
     * 表单密码加密.
     * @param inputPass 用户密码
     * @return 加密密码
     */
    public static String inputPassToFormPass(final String inputPass) {
        String str = "" + SALT.charAt(0) + SALT.charAt(2) + inputPass + SALT.charAt(5) + SALT.charAt(4);
        return md5(str);
    }

    /**
     * 二次加密.
     * @param inputPass 用户密码
     * @param saltDB 数据库盐
     * @return 二次加密后的密码
     */
    public static String inputPassToDbPass(final String inputPass, final String saltDB) {
        String formPass = inputPassToFormPass(inputPass);
        String dbPass = formPassToDBPass(formPass, saltDB);
        return dbPass;
    }


}
