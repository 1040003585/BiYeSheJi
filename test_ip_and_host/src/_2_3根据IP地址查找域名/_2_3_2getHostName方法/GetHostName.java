package _2_3根据IP地址查找域名._2_3_2getHostName方法;

import java.net.InetAddress;
import java.util.Scanner;

public class GetHostName {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Scanner scanner=new Scanner(System.in);
		String host_in=scanner.nextLine();
		
		try {
			InetAddress address=InetAddress.getByName(host_in);
			System.out.println("主机别名:"+address.getHostName());
		
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
