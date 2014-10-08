package org.yi.spider.utils;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

import javax.net.ssl.SSLException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * 
     * <p>
     * 根据URL和文字编码把内容取回来
     * </p>
     * 
     * @param url
     * @param Charset
     * @return
     * @throws Exception 
     */
    public static String getContent(CloseableHttpClient client, String url, String charset, String userAgent) throws Exception {
        try {
        	HttpGet httpGet = new HttpGet(url);
        	httpGet.addHeader("User-Agent", userAgent);
        	
        	CloseableHttpResponse response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            try {
                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode != HttpStatus.SC_OK) {
                	//TODO: 抓取异常则重新构建httpClient， 继续采集
                	if(statusCode != HttpStatus.SC_NOT_FOUND) {
                		
                	}
                    throw new RemoteException("访问对方页面出错， URL:"+url+"，错误码: " + statusCode);
                }
                long start = System.currentTimeMillis();
                String responseBody = EntityUtils.toString(entity, charset);
                logger.debug("URL:"+url+",耗时："+(System.currentTimeMillis()-start));
                return responseBody;
            } catch (IOException e) {
            	throw new IOException("获取目标网页异常， 目标地址："+url, e);
            } finally {
            	response.close();
            }
        } catch (Exception e) {
        	throw new Exception("访问目标站异常， 目标地址："+url, e);
        }
    }
    
    public static CloseableHttpClient buildClient(int timeOut) {
    	HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
    		static final int MAX_RETRY_COUNT = 5;
			@Override
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				if (executionCount >= MAX_RETRY_COUNT) {
		            return false;
		        }
		        if (exception instanceof InterruptedIOException) {
		            return false;
		        }
		        if (exception instanceof UnknownHostException) {
		            return false;
		        }
		        if (exception instanceof ConnectTimeoutException) {
		            return false;
		        }
		        if (exception instanceof SSLException) {
		            return false;
		        }
		        HttpClientContext clientContext = HttpClientContext.adapt(context);
		        HttpRequest request = clientContext.getRequest();
		        boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
		        if (idempotent) {
		            return true;
		        }
		        return false;
			}
		};
		
		RequestConfig requestConfig = RequestConfig.custom()
	            .setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY )
	            .setExpectContinueEnabled(true)
	            .setStaleConnectionCheckEnabled(true)
	            .setSocketTimeout(timeOut)
                .setConnectTimeout(timeOut)
                .setConnectionRequestTimeout(timeOut)
	            .build();
		
    	CloseableHttpClient client = HttpClients.custom()
    			.setRetryHandler(retryHandler)
    			.setDefaultRequestConfig(requestConfig)
    			.build();
    	return client;
    }
    
    public static void closeHttpClient(CloseableHttpClient client) throws IOException {
    	try {
			client.close();
		} catch (IOException e) {
			throw new IOException("关闭连接出错，"+e.getMessage());
		}
    }
    
}
