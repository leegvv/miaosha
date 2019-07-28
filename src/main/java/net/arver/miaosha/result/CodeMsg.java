package net.arver.miaosha.result;

/**
 * 消息封装类.
 */
public final class CodeMsg {

    private int code;
    private String msg;

    // 通用异常 5001XX
    public static final CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static final CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");
    public static final CodeMsg BIND_ERROR = new CodeMsg(500101, "参数异常: %s");

    // 登录模块 5002XX
    public static final CodeMsg SESSION_ERROR = new CodeMsg(500210, "Session不存在或已失效");
    public static final CodeMsg MOBILE_EMPTY = new CodeMsg(500211, "手机号码不能为空");
    public static final CodeMsg PASSWORD_EMPTY = new CodeMsg(500212, "密码不能为空");
    public static final CodeMsg MOBILE_ERROR = new CodeMsg(500213, "手机号码格式错误");
    public static final CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214, "手机号码不存在");
    public static final CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "密码错误");


    // 商品模块 5003XX

    // 订单模块 5004XX

    // 秒杀模块 5005XX
    public static final CodeMsg MIAO_SHA_OVER = new CodeMsg(500500, "商品已经秒杀完毕");
    public static final CodeMsg REPEATE_MIAOSHA = new CodeMsg(500501, "不能重复秒杀");

    private CodeMsg(final int code, final String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public CodeMsg fillArgs(final Object... args) {
        final int code = this.code;
        final String message = String.format(this.msg, args);
        return new CodeMsg(code, message);
    }
}
