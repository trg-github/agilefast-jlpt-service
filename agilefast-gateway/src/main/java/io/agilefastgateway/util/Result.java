/*******************************************************
 * @Title: Result
 * @ProjectName: Java
 * @Package: io.agilefast.common.utils
 * @Description: TODO
 * @author: daixirui
 * @version: V1.0.0
 * @Copyright: 2022 All rights reserved.
 * @date 2022/5/27 13:51</br>
 ********************************************************
 * @date 2022/5/27 13:51
 * @Title: 创建类
 * @version V1.0.0
 * @Description: TODO
/******************************************************/
package io.agilefastgateway.util;

import io.agilefastgateway.api.ResultCode;
import io.swagger.annotations.ApiModelProperty;


public class Result<T> {

    @ApiModelProperty(value = "返回类型")
    private String type = "success";
    @ApiModelProperty(value = "返回对象")
    private  T result;
    @ApiModelProperty(value = "错误编号")
    private Long code;
    @ApiModelProperty(value = "错误信息")
    private String message;

    @ApiModelProperty(value = "时间戳")
    private String timestamp;

    public <T> Result() {
        this.code = ResultCode.SUCCESS.getCode();
        this.type = "success";
        this.message = "";
        this.timestamp = DateUtils.getCurrentDateAsString(DateUtils.DATE_TIME_PATTERN);
    }

    public  Result<T> error(Long code, String message) {
        this.code = code;
        this.type = "error";
        this.message = message;
        this.timestamp = DateUtils.getCurrentDateAsString(DateUtils.DATE_TIME_PATTERN);
        return  this;
    }


    public Result<T> info(Long code, String message, T result) {
        this.code = code;
        this.type = "info";
        this.message = message;
        this.timestamp = DateUtils.getCurrentDateAsString(DateUtils.DATE_TIME_PATTERN);
        this.result = result;
        return this;
    }

    public  Result<T> ok(String message) {
        this.code =  ResultCode.SUCCESS.getCode();
        this.type = "success";
        this.message = message;
        this.timestamp = DateUtils.getCurrentDateAsString(DateUtils.DATE_TIME_PATTERN);
        return this;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Result<T> setData(T result) {
        this.result = result;
        return this;
    }
}
