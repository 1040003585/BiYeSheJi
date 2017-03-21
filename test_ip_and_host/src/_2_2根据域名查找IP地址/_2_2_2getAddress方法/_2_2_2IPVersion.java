package _2_2根据域名查找IP地址._2_2_2getAddress方法;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class _2_2_2IPVersion {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			InetAddress inetAddress=InetAddress.getLocalHost();
			byte []addresses=inetAddress.getAddress();
			if(addresses.length==4){
				System.out.println("The IP version is IPV4");
				int firstbyte=addresses[0];
				if(firstbyte<0){
					firstbyte+=256;
				}
				if((firstbyte&0x80)==0){
					System.out.println("the ip classs is A");
				}else if((firstbyte&0xc0)==0x80){
					System.out.println("the ip class is B");
				}else if((firstbyte&0xe0)==0xc0){
					System.out.println("the ip class is c");
				}else if((firstbyte&0xf0)==0xe0){
					System.out.println("the ip class is d");
				}else if((firstbyte&0xf8)==0xf0){
					System.out.println("the ip class is e");
				}
				for (int address : addresses) {
					address=address<0?address+=256:address;
					System.out.print(address+"+");
				}
			}else{
				System.out.println("The IP version is IPV6");
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
