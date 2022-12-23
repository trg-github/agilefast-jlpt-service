/*******************************************************
 * @Title: R2
 * @ProjectName: Java
 * @Package: io.agilefast.common.utils
 * @Description: TODO
 * @author: daixirui
 * @version: V1.0.0
 * @Copyright: 2022 All rights reserved.
 * @date 2022/5/27 13:54</br>
 ********************************************************
 * @date 2022/5/27 13:54
 * @Title: 创建类
 * @version V1.0.0
 * @Description: TODO
/******************************************************/
package io.agilefastgateway.util;

import io.agilefastgateway.api.ResultCode;

/**
 * @author
 * @Description: 将结果转换为封装后的对象
 * @date 2018/4/19 09:45
 */
public class R2 {

    public R re;
    private final static String SUCCESS = "success";

    public static <T> Result<T> ok() {

        return new Result<T>().ok(SUCCESS);
    }

    public static <T> Result<T> ok(T data) {

        return new Result<T>().setData(data).ok(SUCCESS);
    }

    public static <T> Result<T> err(String message) {

        return new Result<T>().error(ResultCode.FAILED.getCode(), message);
    }
    public static <T> Result<T> errCode(ResultCode code, String message) {

        return new Result<T>().error(code.getCode(), message);
    }

    public static <T> Result<T> data(ResultCode code, String msg, T data) {

        return new Result<T>().info(code.getCode(), msg, data);
    }

    public static <T> Result<T> setOkMsg(String msg){
        return new Result<T>().setData((T) R.ok(msg)).ok(msg);
    }
    public static <T> Result<T> setErrMsg(String msg){
        return new Result<T>().setData((T) R.error(msg)).ok(msg);
    }
}