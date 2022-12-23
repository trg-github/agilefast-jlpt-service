package io.agilefastgateway.filter;

import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.agilefastgateway.config.WebConfig;
import io.agilefastgateway.config.WhiteListConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;


/**
 * @author Administrator
 */
@Configuration
@Slf4j
public class MyOldFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private WebConfig webConfig;
    private WhiteListConfig whiteListConfig;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
        ServletContext servletContext = filterConfig.getServletContext();
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        webConfig = context.getBean(WebConfig.class);
        whiteListConfig = context.getBean(WhiteListConfig.class);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse responses = (HttpServletResponse) servletResponse;
        HttpServletRequest requests = (HttpServletRequest) servletRequest;

        responses.setHeader("Access-Control-Allow-Origin", "*");
        responses.setHeader("Access-Control-Allow-Credentials", "true");
        responses.setHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS,PUT,DELETE,PATCH,HEAD");
        responses.setHeader("Access-Control-Allow-Max-Age", "3600");
        responses.setHeader("Access-Control-Allow-Headers", "*");
        responses.setHeader("Content-Type", "application/json;charset=utf-8");

        String redirectUri = requests.getRequestURI();
        if (ServletUtil.METHOD_OPTIONS.equalsIgnoreCase(requests.getMethod())) {
            responses.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        for (String uri : whiteListConfig.getWhitelist()) {
            if (redirectUri.contains(uri)) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
        }
        redirectUri = redirectUri.replaceAll("hcpjbs", "agilefast-api");

        HttpClient httpClient = null;
        String result = null;
        HttpResponse response = null;
        try {
            RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(10000)
                    .setConnectTimeout(10000).setSocketTimeout(10000).build();
            httpClient = HttpClientBuilder.create().build();
            //获取原始请求参数
            Enumeration<String> parameterNames = requests.getParameterNames();
            //获取原始请求头部参数
            Enumeration<String> headerNames = requests.getHeaderNames();
            if (ServletUtil.METHOD_GET.equalsIgnoreCase(requests.getMethod())) {
                //将原始请求参数设置到新的请求中
                String param = "";
                for (Enumeration e = parameterNames; e.hasMoreElements(); ) {
                    String name = e.nextElement().toString();
                    String value = requests.getParameter(name);
                    param = param + name + "=" + value + "&";
                }
                param = param.length() > 1 ? param.substring(0, param.length() - 1) : param;
                log.info("Get请求穿网地址：" + webConfig.getJlptUrl() + redirectUri + "?" + param);
                HttpGet request = new HttpGet(webConfig.getJlptUrl() + redirectUri + "?" + param);
                //将原有请求头部参数设置到新的request中，并设置X-Auth-Token头部参数
                for (Enumeration e = headerNames; e.hasMoreElements(); ) {
                    String name = e.nextElement().toString();
                    String value = requests.getHeader(name);
                    log.info("Get请求原有穿网的头部参数name：" + name + "-->value：" + value);
                    if ("Content-Length".equalsIgnoreCase(name)) {
                        continue;
                    }
                    if ("Referer".equalsIgnoreCase(name)) {
                        continue;
                    }
                    if ("JL-Auth-Token".equalsIgnoreCase(name)) {
                        request.setHeader("JL-Auth-Token", value);
                        continue;
                    }
                    request.setHeader(name, value);
                }
                request.setHeader("Content-Type", "application/json;charset=utf-8");
                request.setHeader("client_id", webConfig.getClientId());
                request.setHeader("client_secret", webConfig.getClientSecret());
                request.setConfig(config);
                for (Header allHeader : request.getAllHeaders()) {
                    log.info("Get请求穿网的请求头信息name：" + allHeader.getName() + "-->value：" + allHeader.getValue());
                }
                response = httpClient.execute(request);
            } else if (ServletUtil.METHOD_POST.equalsIgnoreCase(requests.getMethod())) {
                //将原始请求参数设置到新的请求中
                JSONObject param = new JSONObject();
                for (Enumeration e = parameterNames; e.hasMoreElements(); ) {
                    String name = e.nextElement().toString();
                    String value = requests.getParameter(name);
                    param.put(name, value);
                }
                StringBuilder data = new StringBuilder();
                String line;
                BufferedReader reader;
                try {
                    reader = requests.getReader();
                    while (null != (line = reader.readLine())) {
                        data.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                log.info("post请求参数：" + data.toString());
                JSONObject jsonObject = JSONObject.parseObject(data.toString());
                log.info("post请求穿网地址：" + webConfig.getJlptUrl() + redirectUri);
                HttpPost request = new HttpPost(webConfig.getJlptUrl() + redirectUri);
                request.setEntity(new StringEntity(jsonObject != null ? jsonObject.toString() : "","utf-8"));

                //将原有请求头部参数设置到新的request中，并设置X-Auth-Token头部参数
                for (Enumeration e = headerNames; e.hasMoreElements(); ) {
                    String name = e.nextElement().toString();
                    String value = requests.getHeader(name);
                    log.info("post请求原有穿网的头部参数name：" + name + "-->value：" + value);
                    if ("Content-Length".equalsIgnoreCase(name)) {
                        continue;
                    }
                    if ("Referer".equalsIgnoreCase(name)) {
                        continue;
                    }
                    if ("JL-Auth-Token".equalsIgnoreCase(name)) {
                        request.setHeader("JL-Auth-Token", value);
                        continue;
                    }
                    request.setHeader(name, value);
                }
                request.setHeader("Content-Type", "application/json;charset=utf-8");
                request.setHeader("client_id", webConfig.getClientId());
                request.setHeader("client_secret", webConfig.getClientSecret());
                for (Header allHeader : request.getAllHeaders()) {
                    log.info("post请求穿网的请求头信息name：" + allHeader.getName() + "-->value：" + allHeader.getValue());
                }
                request.setConfig(config);
                response = httpClient.execute(request);
            }
            if(redirectUri.equalsIgnoreCase("/agilefast-api/api/codeManagement/register/contract/getFileAttachmentlistByParams")){
                response.getEntity().writeTo(responses.getOutputStream());
            }else{
                //设置原responses的返回头部参数
                Header[] allHeaders = response.getAllHeaders();
                for (Header header : allHeaders) {
                    if ("Transfer-Encoding".equalsIgnoreCase(header.getName())) {
                        continue;
                    }
                    responses.setHeader(header.getName(), header.getValue());
                }
                //将请求结果写入原responses中返回到前端
                int statusCode = response.getStatusLine().getStatusCode();
                log.info("穿网返回的状态码：" + statusCode);
                result = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
                responses.setStatus(statusCode);
                log.info("穿网返回的结果：" + result);
                responses.getWriter().write(result);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
