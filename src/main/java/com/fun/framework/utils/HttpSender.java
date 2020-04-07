package com.fun.framework.utils;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;

public class HttpSender {
	
	/**
	 *   Post 方式 发送普通短信
	 * @param url
	 *            应用地址，类似于http://api2.santo.cc/submit
	 * @param cpid
	 *            账号
	 * @param cppwd
	 *            密码
	 * @param da
	 *            手机号码，多个号码使用","分割
	 * @param sm
	 *            短信内容
	 * @return 返回值定义参见HTTP协议文档
	 * @throws Exception
	 */
	public static String SendPost(String url, String command, String cpid, String cppwd, String da, String sm,
			String sa) throws Exception {
		HttpClient client = new HttpClient(new HttpClientParams(), new SimpleHttpConnectionManager());
		PostMethod method = new PostMethod();
		try {
			URI base = new URI(url, false);
			method.setURI(new URI(base, "", false));
			method.setRequestBody(new NameValuePair[] {
					new NameValuePair("command", command),
					new NameValuePair("cpid", cpid),
					new NameValuePair("cppwd", cppwd),
					new NameValuePair("da", da),
					new NameValuePair("sm", sm),
					new NameValuePair("sa", sa) });
			HttpMethodParams params = new HttpMethodParams();
			params.setContentCharset("UTF-8");
			method.setParams(params);
			int result = client.executeMethod(method);
			if (result == HttpStatus.SC_OK) {
				InputStream in = method.getResponseBodyAsStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = in.read(buffer)) != -1) {
					baos.write(buffer, 0, len);
				}
				in.close();// 1
				baos.close();// 2
				return URLDecoder.decode(baos.toString(), "UTF-8");
			} else {
				throw new Exception("HTTP ERROR Status: " + method.getStatusCode() + ":" + method.getStatusText());
			}
		} finally {
			method.releaseConnection();
		}
	}
	
	/**
	 *   get 发送普通短信
	 * @param url
	 *            应用地址，类似于http://api2.santo.cc/submit
	 * @param cpid
	 *            账号
	 * @param cppwd
	 *            密码
	 * @param da
	 *            手机号码，多个号码使用","分割
	 * @param sm
	 *            短信内容
	 * @return 返回值定义参见HTTP协议文档
	 * @throws Exception
	 */
	public static String SendGet(String url, String command, String cpid, String cppwd, String da, String sm,
								 String sa) throws Exception {
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod();
		try {
			URI base = new URI(url, false);
			method.setURI(new URI(base, "", false));
			method.setQueryString(new NameValuePair[] {
					new NameValuePair("command", command),
					new NameValuePair("cpid", cpid),
					new NameValuePair("cppwd", cppwd),
					new NameValuePair("da", da),
					new NameValuePair("sm", sm),
					new NameValuePair("sa", sa) });
			HttpMethodParams params = new HttpMethodParams();
			params.setContentCharset("UTF-8");
			method.setParams(params);
			int result = client.executeMethod(method);
			if (result == HttpStatus.SC_OK) {
				InputStream in = method.getResponseBodyAsStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = in.read(buffer)) != -1) {
					baos.write(buffer, 0, len);
				}
				in.close();// 1
				baos.close();// 2
				return URLDecoder.decode(baos.toString(), "UTF-8");
			} else {
				throw new Exception("HTTP ERROR Status: " + method.getStatusCode() + ":" + method.getStatusText());
			}
		} finally {
			method.releaseConnection();
		}
	}
	
	
	/**
	 * 查询余额   
	 * @param url
	 * @param cpid
	 * @param cppwd
	 * @return
	 * @throws Exception
	 */
	public static String query(String url, String cpid, String cppwd) throws Exception {
		HttpClient client = new HttpClient(new HttpClientParams(), new SimpleHttpConnectionManager(true));
		GetMethod method = new GetMethod();
		try {
			URI base = new URI(url, false);
			method.setURI(base);
			method.setQueryString(new NameValuePair[] { 
					new NameValuePair("cpid", cpid),
					new NameValuePair("cppwd", cppwd)});
			int result = client.executeMethod(method);
			if (result == HttpStatus.SC_OK) {
				InputStream in = method.getResponseBodyAsStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = in.read(buffer)) != -1) {
					baos.write(buffer, 0, len);
				}
				in.close();// 1
				baos.close();// 2
				return URLDecoder.decode(baos.toString(), "UTF-8");
			} else {
				throw new Exception("HTTP ERROR Status: " + method.getStatusCode() + ":" + method.getStatusText());
			}
		} finally {
			method.releaseConnection();
		}
	}

	public static byte[] hexStr2ByteArr(String strIn) throws Exception {
		byte[] arrB = strIn.getBytes();
		int iLen = arrB.length;
		// 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}

	public static String decodeHexStr(String hexStr) {
		String realStr = null;
		if (hexStr != null) {
			try {
				byte[] data = hexStr2ByteArr(hexStr);
				realStr = new String(data, "GBK");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return realStr;
	}
}
