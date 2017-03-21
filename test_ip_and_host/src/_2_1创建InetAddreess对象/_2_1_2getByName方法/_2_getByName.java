package _2_1创建InetAddreess对象._2_1_2getByName方法;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class _2_getByName {

	/**
	 * @param args
	 *  java CreateInetAddressObject._2_getByName www.baidu.com
	 *  java CreateInetAddressObject._2_getByName localhost
	 *  java CreateInetAddressObject._2_getByName wubeing-EC18-Series
	 *  java CreateInetAddressObject._2_getByName 192.168.1.1
	 *  
	 *  C:\WINDOWS\system32\drivers\etc 鐨勬枃浠秇osts涓坊鍔狅細192.168.1.100 www.mysite.com
	 *  java CreateInetAddressObject._2_getByName www.mysite.com
	 * 
	 *  output:
	 *  www.baidu.com/119.75.217.109
	 */
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.out.print("please input args");
			return;
		}
		String host = args[0];
		try {
			InetAddress address = InetAddress.getByName(host);
			System.out.println(address);
		} catch (UnknownHostException el) {
			// TODO Auto-generated catch block
			el.printStackTrace();
		}
	}

}
