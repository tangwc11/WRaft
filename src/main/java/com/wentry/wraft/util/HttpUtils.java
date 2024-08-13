package com.wentry.wraft.util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * @Description:
 * @Author: tangwc
 */
public class HttpUtils {

    public static String get(String url) {
        // 创建 HttpClient 实例
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 创建 HttpGet 实例
        HttpGet request = new HttpGet(url);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            // 获取响应状态码和内容
            int statusCode = response.getStatusLine().getStatusCode();
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
        }
        return null;
    }
}
