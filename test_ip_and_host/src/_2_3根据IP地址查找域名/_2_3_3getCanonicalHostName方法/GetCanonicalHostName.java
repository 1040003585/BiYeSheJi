package _2_3根据IP地址查找域名._2_3_3getCanonicalHostName方法;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class GetCanonicalHostName {

	public static void outHostName(InetAddress address,String s){
		System.out.println("通过："+s+"创建InetAdress对象");
		System.out.println("主机名："+address.getCanonicalHostName());
		System.out.println("主机别名："+address.getHostAddress());
		System.out.println("");
		
	}
	
	public static void main(String[] args) throws UnknownHostException {
		//1
		outHostName(InetAddress.getLocalHost(), "getLocalHost方法");
		//2 与dns有关
		outHostName(InetAddress.getByName("www.ibm.com"), "www.ibm.com");
		outHostName(InetAddress.getByName("www.123.com"), "www.123.com");
		outHostName(InetAddress.getByName("www.ycit.com"), "www.ycit.com");
		//3
		outHostName(InetAddress.getByName("202.192.133.1"), "202.192.133.1");
		outHostName(InetAddress.getByName("202.192.133.1"), "202.192.133.1");
		outHostName(InetAddress.getByName("202.192.133.1"), "202.192.133.1");
		
			
	}
}
