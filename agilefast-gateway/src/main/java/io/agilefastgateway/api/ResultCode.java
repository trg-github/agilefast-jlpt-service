package io.agilefastgateway.api;


/**
 * @program: agilefast-framework
 * @description: 常用API返回对象
 * @author: yehao
 * @create: 2022-07-20 13:54
 **/
public enum ResultCode implements IErrorCode {
    SUCCESS(200, "请求成功"),
    FAILED(500, "未知异常,请联系管理员"),
    VALIDATE_FAILED(404, "参数检验失败"),
    UNAUTHORIZED(201, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限");
    private long code;
    private String message;

    private ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public long getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
