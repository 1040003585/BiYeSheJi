package _2_2根据域名查找IP地址._2_2_1getHostAddress方法;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String host = "www.nju.edu.cn";// "localhost";
		if (args.length == 1) {
			host = args[0];
		}

		InetAddress ad;
		try {
			ad = InetAddress.getByName(host);
			System.out.println("getHostAddress:" + ad.getHostAddress());
			System.out.println("getCanonicalHostName:"
					+ ad.getCanonicalHostName());
			System.out.println("getHostName:" + ad.getHostName());
			System.out.println("isLoopbackAddress:" + ad.isLoopbackAddress());
			System.out.println("isLinkLocalAddress:" + ad.isLinkLocalAddress());
			System.out.println("isAnyLocalAddress:" + ad.isAnyLocalAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
