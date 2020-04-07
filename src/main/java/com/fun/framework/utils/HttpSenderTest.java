package com.fun.framework.utils;

import java.io.UnsupportedEncodingException;
/**
 * 普通短信发送测试Main 
 * @author 
 *
 */
public class HttpSenderTest {
	
	private static String cpid=""; // 发送短信的账号(非登录账号)
	private static String cppwd="";// 发送短信的密码(非登录密码)

	//调用Main() 方法前记得更改 —— 电话号码、 账号、密码  等信息
	public static void main(String[] args) throws UnsupportedEncodingException {
		
		sendMessage();//调用发送普通短信方法

//		getBalance();//查询余额
	}
	
	//发送普通短信方法
	public static void sendMessage(){

		String url = "http://api2.santo.cc/submit";  //应用地址 (无特殊情况时无需修改)
		String command = "MT_REQUEST";     	// MT_REQUEST：短信  VO_REQUEST：语音
		String da = "861571*****82"; //目标号码 如中国大陆：8613800000000(支持多号码批量提交，多个号码之间用半角逗号分隔，最多100个)
		String sm = "您的验证码是872211" ; //短信内容(URL utf-8编码)
		String sa = null;     	//自定义发送者号码 (仅限数字或者字母,纯数字支持最大16个字符,带有字母支持最大11个字符)

		try {
			    String returnString2 = HttpSender.SendPost(url,command,cpid, cppwd,da,sm,sa);
//			    String returnString2 = HttpSender.SendGet(url,command,cpid, cppwd,da,sm,sa);
			    System.out.println(returnString2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	//查询账户余额
	public static void getBalance() {

		String url = "http://api2.santo.cc/get-balance";// 应用地址(无特殊情况时无需修改)
		try {
			String returnString = HttpSender.query(url,cpid, cppwd);
			System.out.println(returnString);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
