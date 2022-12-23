package io.agilefastgateway.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @packageName: com.fh.util
 * @ClassName: HttpRequestUtil
 * @Description: 【模拟post请求】
 * @author lujc
 * @date 2016年12月8日 上午10:57:06
 *
 */
public class HttpRequestUtil {

	private static final int AEFAULTTIMEOUT = 3000;

	public static String sendPost(String url, String data) {
		return sendPost(url, data, AEFAULTTIMEOUT);
	}

	/**
	 * @Title: sendPost @Description: 发送post请求获取数据 @param @param url
	 * 请求地址 @param @param json 数据发送的参数 @param @return @return String @throws
	 */
	public static String sendPost(String url, String data, int timeOut) {
		HttpClient httpClient = null;
		HttpPost request = null;
		String result = null;
		HttpResponse response = null;
		try {
			RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(timeOut)
					.setConnectTimeout(timeOut).setSocketTimeout(timeOut).build();
			httpClient = HttpClientBuilder.create().build();
			StringEntity params = new StringEntity(data, "UTF-8");
			params.setContentType("application/json");
			request = new HttpPost(url);
			request.setConfig(config);
			request.setEntity(params);
			response = httpClient.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, "UTF-8");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * @Title: sendPost @Description: url参数传递数据 @param @param url @param @param
	 * praamDatas @param @return @return String @throws
	 */
	public static String sendPost(String url, Map<String, String> paramDatas) {
		return sendPost(url, paramDatas, AEFAULTTIMEOUT);
	}

	public static String sendPost(String url, Map<String, String> paramDatas, int timeOut) {
		HttpClient httpClient = null;
		HttpPost request = null;
		String result = null;
		HttpResponse response = null;
		try {
			RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(timeOut)
					.setConnectTimeout(timeOut).setSocketTimeout(timeOut).build();
			httpClient = HttpClientBuilder.create().build();
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			for (String name : paramDatas.keySet()) {
				list.add(new BasicNameValuePair(name, paramDatas.get(name)));
			}
			request = new HttpPost(url);
			request.setConfig(config);
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
			entity.setContentType("application/x-www-form-urlencoded");
			request.setEntity(entity);
			response = httpClient.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, "UTF-8");
				}
			} else {
				result = EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public static String sendJsonPost(String url, JSONObject paramData) {
		return sendJsonPost(url, paramData, AEFAULTTIMEOUT);
	}

	/**
	 * @Title: sendPost @Description: 发送post请求格式的数据 参数为json对象 @param @param
	 * url @param @param paramData @param @return @return String @throws
	 */
	public static String sendJsonPost(String url, JSONObject paramData, int timeOut) {
		HttpClient httpClient = null;
		HttpPost request = null;
		String result = null;
		HttpResponse response = null;
		try {
			RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(timeOut)
					.setConnectTimeout(timeOut).setSocketTimeout(timeOut).build();
			httpClient = HttpClientBuilder.create().build();
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			for (Object name : paramData.keySet()) {
				list.add(new BasicNameValuePair(name.toString(), paramData.getString(name.toString())));
			}
			request = new HttpPost(url);
			request.setConfig(config);
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
			request.setEntity(entity);
			response = httpClient.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, "UTF-8");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public static String sendGet(String url, String param) {
		return sendGet(url, param, AEFAULTTIMEOUT);
	}

	/**
	 * 向指定URL发送GET方法的请求
	 *
	 * @param url   发送请求的URL
	 * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String sendGet(String url, String param, int timeOut) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url + "?" + param;
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setConnectTimeout(timeOut);
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			// Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			/*
			 * for (String key : map.keySet()) { System.out.println(key + "--->" +
			 * map.get(key)); }
			 */
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			// System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}
}
