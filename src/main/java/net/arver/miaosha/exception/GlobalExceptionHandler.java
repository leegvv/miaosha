package net.arver.miaosha.exception;

import net.arver.miaosha.result.CodeMsg;
import net.arver.miaosha.result.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 全局异常处理.
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    /**
     * 异常处理.
     * @param request 请求
     * @param e 异常
     * @return 错误信息
     */
    @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(final HttpServletRequest request, final Exception e) {
        e.printStackTrace();
        if (e instanceof GlobalException) {
            final GlobalException ex = (GlobalException) e;
            return Result.error(ex.getCodeMsg());
        } else if (e instanceof BindException) {
            final BindException ex = (BindException) e;
            final List<ObjectError> errors = ex.getAllErrors();
            final ObjectError error = errors.get(0);
            final String msg = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
        }
        return Result.error(CodeMsg.SERVER_ERROR);
    }
}
