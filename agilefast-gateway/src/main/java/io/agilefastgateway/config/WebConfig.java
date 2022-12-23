/*******************************************************
 *Copyright (c) 2017 All Rights Reserved.
 *JDK版本： 1.8
 *公司名称：
 *命名空间：io.jeasyframework.config
 *文件名：  SiteConfig
 *版本号：  V1.0.0.0
 *创建人：  daixirui
 *电子邮箱：daixirui@live.com
 *创建时间：2017/06/20 16:26
 *描述：
 *
 *=====================================================
 *修改标记
 *修改时间：2017/06/20 16:26
 *修改人：  daixirui
 *版本号：  V1.0.0.0
 *描述：
 *
 /******************************************************/
package io.agilefastgateway.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.Getter;
import org.apache.http.entity.StringEntity;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 */
@Data
@Component
@Getter
@ConfigurationProperties(prefix = "web.config")
public class WebConfig {

    private String webLoginUrl;
    private String jlptUrl;
    private String ssoLoginUrl;
    private String tokenUrl;
    private String clientId;
    private String clientSecret;
    private String grantType;
    private String username;
    private String password;


    public static void main(String[] args) {
        String a = "[{'no':11,'encodeDetailId':'H1350041021420122100002051','accessoryHotNo':'2','erpMesId':'3','manufactureDate':'572630416','accessoryDetailId':'1585471783119667201','accessoryEncodeId':'684EEAB432354D4CB911F2D39F585156','accessoryInfoId':'396F584223194C0F85151748818775DE'},{'no':11,'encodeDetailId':'H1350041021420122100002063','accessoryHotNo':'2','erpMesId':'4','manufactureDate':'572630416','accessoryDetailId':'1585471783119667202','accessoryEncodeId':'684EEAB432354D4CB911F2D39F585156','accessoryInfoId':'396F584223194C0F85151748818775DE'},{'no':11,'encodeDetailId':'H1350041021420122100002070','accessoryHotNo':'2','erpMesId':'5','manufactureDate':'572630416','accessoryDetailId':'1585471783119667203','accessoryEncodeId':'684EEAB432354D4CB911F2D39F585156','accessoryInfoId':'396F584223194C0F85151748818775DE'}]";
        if(a.startsWith("[")){
            JSONArray jsonArray = JSONObject.parseArray(a);
            System.out.println(jsonArray.toString());
        }
        if(a.startsWith("{")){
            JSONObject pramasJson = JSONObject.parseObject(a);
            System.out.println(pramasJson);
        }

    }
}
