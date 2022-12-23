package io.agilefastgateway.controller;


import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import io.agilefastgateway.config.WebConfig;
import io.agilefastgateway.form.FreeLoginForm;
import io.agilefastgateway.util.R2;
import io.agilefastgateway.util.Result;
import io.agilefastgateway.util.SecretUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Administrator
 */
@RestController("loginController")
@RequestMapping("/api")
@Api(tags = "机辆平台单点登录")
@EnableScheduling
@Slf4j
public class LoginController {

    @Autowired
    private WebConfig webConfig;

    private static String COOKIE_NANE = "YdzbMainWeb_cookie";
    private static long TIME_OUT = 60 * 1000;

    private List<Map<String, Object>> loginList = new ArrayList<>();

    @RequestMapping("ssoLogin")
    @ApiOperation("Api登录授权")
    public void ssoLogin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        //获取登录用户信息
        String deUser = request.getHeader("user");

        if (deUser == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "用户信息获取失败");
            return;
        }
        String userStr = Base64Decoder.decodeStr(deUser);
        log.info("发送过来的用户信息：" + userStr);
        JSONObject user = JSONObject.parseObject(userStr);
        String username = String.valueOf(user.get("account"));

        //获取JL-Auth-Token信息
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "token获取失败");
            return;
        }
        log.info("发送过来的cookies信息：" + cookies.toString());
        String jlAuthToken = "";
        for (Cookie cookie : cookies) {
            if (COOKIE_NANE.equalsIgnoreCase(cookie.getName())) {
                jlAuthToken = cookie.getValue();
            }
        }
        if (jlAuthToken.isEmpty()) {
            response.sendError(404, "token获取失败");
            return;
        }
        log.info("发送过来的token信息：" + jlAuthToken);
        try {
            Map<String, String> params = new HashMap<>(10);
            params.put("username", username);
            log.info("请求内网服务免密登地址：" + webConfig.getJlptUrl()+webConfig.getSsoLoginUrl());
            String resultStr = HttpRequest.post(webConfig.getJlptUrl()+webConfig.getSsoLoginUrl())
                    .body(JSONObject.toJSONString(params))
                    .header("client_id", webConfig.getClientId())
                    .header("client_secret", webConfig.getClientSecret())
                    .header("JL-Auth-Token", jlAuthToken)
                    .header("Content-Type", "application/json")
                    .execute().body();
            log.info("Api免密登录成功，返回结果信息：" + resultStr);
            //保存用户登录信息，生成code
            Map<String, Object> resultMap = JSONObject.parseObject(resultStr, Map.class);
            int resultCode = Integer.parseInt(resultMap.get("code").toString());
            if (HttpServletResponse.SC_OK == resultCode) {
                String code = UUID.randomUUID().toString().substring(0, 6);
                Object loginUser = resultMap.get("result");
                Map<String, Object> loginMsg = new HashMap<>();
                loginMsg.put("code", code);
                loginMsg.put("user", loginUser);
                loginMsg.put("JL-Auth-Token", jlAuthToken);
                loginMsg.put("creatTimeStamp", String.valueOf(System.currentTimeMillis()));
                loginList.add(loginMsg);
                log.info("重定向登录页地址：" + request.getContextPath() + webConfig.getWebLoginUrl() + "?code=" + Base64Encoder.encode(code));
                response.sendRedirect(webConfig.getWebLoginUrl() + "?code=" + Base64Encoder.encode(code));
            } else {
                response.sendError(resultCode, "用户登录异常：" + resultMap.get("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "用户登录异常：" + e.getMessage());
        }
    }

    @RequestMapping("getLoginUser")
    @ApiOperation("免密登录授权")
    public Result<Map<String, Object>> getLoginUser(@RequestBody FreeLoginForm form) {
        log.info("请求的授权码：" + form.getCode());
        log.info("用户登录成功的loginList：" + loginList.toString());
        String code = form.getCode();
        code = SecretUtil.desEncrypt(code);
        code = Base64Decoder.decodeStr(code);
        Map<String, Object> loginMsg = null;
        synchronized (new Object()) {
            for (int i = 0; i < loginList.size(); i++) {
                loginMsg = loginList.get(i);
                if (code.equals(loginMsg.get("code"))) {
                    break;
                }
            }
            if (loginMsg == null) {
                return R2.err("该用户未登录或登录失效！");
            }
        }
        Map<String, Object> user = JSONObject.parseObject(loginMsg.get("user").toString(), Map.class);
        user.put("authToken", loginMsg.get("JL-Auth-Token"));
        log.info("用户登录成功返回的user：" + user.toString());
        return R2.ok(user);
    }

    @Scheduled(fixedDelay = 60 * 1000)
    public void clearTimeoutInfo() {
        synchronized (new Object()) {
            List<Map<String, Object>> timeoutList = new ArrayList<>();
            for (int i = 0; i < loginList.size(); i++) {
                Map<String, Object> loginMsg = loginList.get(i);
                long creatTime = Long.parseLong(loginMsg.get("creatTimeStamp").toString());
                if (creatTime + TIME_OUT < System.currentTimeMillis()) {
                    timeoutList.add(loginMsg);
                }
            }
            loginList.removeAll(timeoutList);
        }
    }

}
