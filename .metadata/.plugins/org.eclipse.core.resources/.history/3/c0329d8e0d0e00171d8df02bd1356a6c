package _2_2根据域名查找IP地址._2_2_1getHostAddress方法;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class getHostAddress {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length==0){
			System.out.println("Please input args");
			return ;
		}
		try {
			InetAddress address=InetAddress.getByName(args[0]);
			System.out.println("IP地址为："+address.getHostAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*output:
	 * java FindIpByDomain._1_getHostAddress www.hzu.edu.cn
	 * IP地址为：59.33.247.100

	 */
}
