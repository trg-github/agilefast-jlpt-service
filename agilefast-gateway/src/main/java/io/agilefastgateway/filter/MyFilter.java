package io.agilefastgateway.filter;

import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.agilefastgateway.config.WebConfig;
import io.agilefastgateway.config.WhiteListConfig;
import io.agilefastgateway.util.R2;
import io.agilefastgateway.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;


/**
 * @author Administrator
 */
@Configuration
@Slf4j
public class MyFilter implements Filter {

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

        HttpResponse response = null;

        try {
            if (ServletUtil.METHOD_GET.equalsIgnoreCase(requests.getMethod())) {
                response=sendGet(requests);
            } else if (ServletUtil.METHOD_POST.equalsIgnoreCase(requests.getMethod())) {
                response=sendPost(requests);
            }
            response.getEntity().writeTo(responses.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    public HttpResponse sendGet(HttpServletRequest request) throws IOException {
        //将原始请求参数设置到新的请求中
        String param = "";
        for (Enumeration e = request.getParameterNames(); e.hasMoreElements(); ) {
            String name = e.nextElement().toString();
            String value = request.getParameter(name);
            param = param + name + "=" + value + "&";
        }
        param = param.length() > 1 ? param.substring(0, param.length() - 1) : param;
        log.info("Get请求穿网地址：" + webConfig.getJlptUrl() + request.getRequestURI().replaceAll("hcpjbs", "agilefast-api") + "?" + param);
        HttpGet httpGet = new HttpGet(webConfig.getJlptUrl() + request.getRequestURI().replaceAll("hcpjbs", "agilefast-api") + "?" + param);
        //将原有请求头部参数设置到新的request中，并设置X-Auth-Token头部参数
        for (Enumeration e = request.getHeaderNames(); e.hasMoreElements(); ) {
            String name = e.nextElement().toString();
            String value = request.getHeader(name);
            log.info("Get请求原有穿网的头部参数name：" + name + "-->value：" + value);
            if ("Content-Length".equalsIgnoreCase(name)) {
                continue;
            }
            if ("Referer".equalsIgnoreCase(name)) {
                continue;
            }
            if ("JL-Auth-Token".equalsIgnoreCase(name)) {
                httpGet.setHeader("JL-Auth-Token", value);
                continue;
            }
            httpGet.setHeader(name, value);
        }
        httpGet.setHeader("Content-Type", "application/json;charset=utf-8");
        httpGet.setHeader("client_id", webConfig.getClientId());
        httpGet.setHeader("client_secret", webConfig.getClientSecret());
        //设置请求超时
        RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(10000)
                .setConnectTimeout(10000).setSocketTimeout(10000).build();
        httpGet.setConfig(config);
        //打印请求头部参数
        for (Header allHeader : httpGet.getAllHeaders()) {
            log.info("Get请求穿网的请求头信息name：" + allHeader.getName() + "-->value：" + allHeader.getValue());
        }
        HttpClient httpClient = HttpClientBuilder.create().build();
        return httpClient.execute(httpGet);
    }
    public HttpResponse sendPost(HttpServletRequest request) throws IOException, ServletException {
        //创建新的Post请求
        HttpPost httpPost = new HttpPost(webConfig.getJlptUrl() + request.getRequestURI().replaceAll("hcpjbs", "agilefast-api"));
        //将原有请求头部参数设置到新的request中，并设置X-Auth-Token头部参数
        for (Enumeration e = request.getHeaderNames(); e.hasMoreElements(); ) {
            String name = e.nextElement().toString();
            String value = request.getHeader(name);
            log.info("post请求原有穿网的头部参数name：" + name + "-->value：" + value);
            if ("content-type".equalsIgnoreCase(name)) {
                continue;
            }
            if ("Content-Length".equalsIgnoreCase(name)) {
                continue;
            }
            if ("Referer".equalsIgnoreCase(name)) {
                continue;
            }
            if ("JL-Auth-Token".equalsIgnoreCase(name)) {
                httpPost.setHeader("JL-Auth-Token", value);
                continue;
            }
            httpPost.setHeader(name, value);
        }
        httpPost.setHeader("client_id", webConfig.getClientId());
        httpPost.setHeader("client_secret", webConfig.getClientSecret());
        for (Header allHeader : httpPost.getAllHeaders()) {
            log.info("post请求穿网的请求头信息name：" + allHeader.getName() + "-->value：" + allHeader.getValue());
        }
        //判断是否是文件上传请求
        if (ServletFileUpload.isMultipartContent(request)) {
            log.info("文件上传请求");
            //将原始请求参数设置到新的请求中
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            multipartEntityBuilder.setMode(HttpMultipartMode.RFC6532);
            for (Enumeration e = request.getParameterNames(); e.hasMoreElements(); ) {
                String name = e.nextElement().toString();
                String value = request.getParameter(name);
                multipartEntityBuilder.addTextBody(name, value,ContentType.APPLICATION_JSON);
            }
            //获取请求中的文件信息，并添加到新的post请求中
            for (Iterator<Part> iterator = request.getParts().iterator(); iterator.hasNext(); ) {
                Part part = iterator.next();
                if (part.getSubmittedFileName() != null) {
                    //获取文件名
                    String fileName = part.getSubmittedFileName();
                    //获取文件流
                    InputStream fileStream = part.getInputStream();
                    multipartEntityBuilder.addBinaryBody("files", fileStream, ContentType.MULTIPART_FORM_DATA, fileName);
                    log.info("上传文件名："+fileName);
                    log.info("上传文件流："+fileStream.toString());
                }
            }
            httpPost.setEntity(multipartEntityBuilder.build());

            //设置请求超时
            RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(10000)
                    .setConnectTimeout(10000).setSocketTimeout(10000).build();
            httpPost.setConfig(config);
            //创建HttpClient，执行请求并返回response
            log.info("post请求穿网地址：" + httpPost.getURI());
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            return httpClient.execute(httpPost);
        } else {
            //不是文件上传请求
            httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
            StringBuilder pramas = new StringBuilder();
            String line;
            BufferedReader reader;
            reader = request.getReader();
            while (null != (line = reader.readLine())) {
                pramas.append(line);
            }
            log.info("post请求参数：" + pramas.toString());
            if(pramas.toString().startsWith("[")){
                log.info("post请求参数类型为数组");
                JSONArray jsonArray = JSONObject.parseArray(pramas.toString());
                httpPost.setEntity(new StringEntity(jsonArray != null ? jsonArray.toString() : "", "utf-8"));
            }else if(pramas.toString().startsWith("{")){
                log.info("post请求参数类型为对象");
                JSONObject pramasJson = JSONObject.parseObject(pramas.toString());
                httpPost.setEntity(new StringEntity(pramasJson != null ? pramasJson.toString() : "", "utf-8"));
            }else{
                log.info("post请求参数类型为其他");
                httpPost.setEntity(new StringEntity(pramas != null ? pramas.toString() : "", "utf-8"));
            }

            //设置请求超时
            RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(10000)
                    .setConnectTimeout(10000).setSocketTimeout(10000).build();
            httpPost.setConfig(config);
            //创建HttpClient，执行请求并返回response
            log.info("post请求穿网地址：" + httpPost.getURI());
            HttpClient httpClient=HttpClientBuilder.create().build();
            return httpClient.execute(httpPost);
        }
    }


}
