package net.arver.miaosha.exception;

import net.arver.miaosha.result.CodeMsg;

/**
 * 全局异常.
 */
public class GlobalException extends RuntimeException {

    private CodeMsg codeMsg;

    public GlobalException(final CodeMsg codeMsg) {
        super(codeMsg.getMsg());
        this.codeMsg = codeMsg;
    }

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }

}
