package _2_2根据域名查找IP地址._2_2_1getHostAddress方法;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class GetIP {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			InetAddress ad=InetAddress.getByName("www.nju.edu.cn");
			System.out.println("IP地址为:"+ad.getHostAddress());
				
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
