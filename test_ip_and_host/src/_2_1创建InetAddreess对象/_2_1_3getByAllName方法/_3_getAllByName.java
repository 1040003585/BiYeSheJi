package _2_1创建InetAddreess对象._2_1_3getByAllName方法;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class _3_getAllByName {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		if (args.length == 0) {
//			System.out.println("please input args");
//			return;
//		}
		Scanner scanner = new Scanner(System.in);
		String hostString=scanner.nextLine();
		
		try {
			InetAddress addresses[]=InetAddress.getAllByName(hostString);
			for(InetAddress address :addresses){
				System.out.println(address);
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*output:
	 * java CreateInetAddressObject._3_getAllByName www.csdn.net
	 * www.csdn.net/101.201.172.229
	 * 
	 * java CreateInetAddressObject._3_getAllByName www.baidu.com
	 * www.baidu.com/14.215.177.37
	 * www.baidu.com/14.215.177.38
	 */

}
