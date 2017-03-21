package _2_4Inet4Address类和Inet6Address类;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 
 * Copyright ? 2016 Authors. All rights reserved.
 *
 * FileName: .java
 * @author : Wu_Being <1040003585@qq.com>
 * Date/Time: 2016-6-10/下午09:41:35
 * Description:
 */
public class MyIPType {

	public static void main(String[] args) {
		if (args.length == 0) {
			return;
		}

		InetAddress address;
		try {
			address = InetAddress.getByName(args[0]);
			System.out.println("IP:" + address.getHostAddress());
			// 方法一：
			switch ((address.getAddress().length)) {
			case 4:
				System.out.println("根据byte数组长度判断这个IP地址是IPv4地址");
				break;
			case 6:
				System.out.println("根据byte数组长度判断这个IP地址是IPv6地址");
				break;
			default:
				break;
			}
			// 方法二：
			if (address instanceof Inet4Address) {

				System.out.println("使用instanceof判断这个IP地址是IPv4地址");
			} else if (address instanceof Inet6Address) {
				System.out.println("使用instanceof判断这个IP地址是IPv6地址");
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}
}
