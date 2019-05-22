package net.arver.miaosha.result;

/**
 * 结果封装类
 *
 * @param <T> 数据类型
 */
public final class Result<T> {

    private int code;
    private String msg;
    private T data;

    /**
     * 成功封装方法
     *
     * @param data 数据
     * @param <T>  类型
     * @return 包装结果
     */
    public static <T> Result<T> success(final T data) {
        return new Result<>(data);
    }

    /**
     * 失败封装方法
     *
     * @param codeMsg 错误消息封装类
     * @param <T>     类型
     * @return 包装结果
     */
    public static <T> Result<T> error(final CodeMsg codeMsg) {
        return new Result<T>(codeMsg);
    }

    private Result(T data) {
        this.data = data;
        this.code = 0;
        this.msg = "success";
    }

    private Result() {
        this.code = 0;
        this.msg = "success";
    }

    private Result(final CodeMsg codeMsg) {
        if (codeMsg == null) {
            return;
        }
        this.code = codeMsg.getCode();
        this.msg = codeMsg.getMsg();
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
}
