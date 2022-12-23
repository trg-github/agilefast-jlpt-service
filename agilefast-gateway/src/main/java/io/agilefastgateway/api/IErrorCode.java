package io.agilefastgateway.api;

/**
 * @program: agilefast-framework
 * @description: 常用API返回对象接口
 * @author: yehao
 * @create: 2022-07-20 13:54
 **/
public interface IErrorCode {
    /**
     * 返回码
     */
    long getCode();

    /**
     * 返回信息
     */
    String getMessage();
}
