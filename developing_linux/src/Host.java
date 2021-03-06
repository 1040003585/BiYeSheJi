/**
 * [*] Host: www.baidu.com:443
 * 
 * @author wu_being
 * 
 */
final public class Host {

	public String host;
	public String address;
	public int port;

	public boolean cal() {
		if (host == null) {
			return false;
		}
		int start = host.indexOf(": ");// :
		if (start == -1) {
			return false;
		}
		int next = host.indexOf(':', start + 2);// 127
		if (next == -1) {// localhost: 127.0.0.1 , not port
			port = 80;
			address = host.substring(start + 2);// 127.0.0.1
		} else {
			address = host.substring(start + 2, next);
			// port = Integer.valueOf(host.substring(next + 1));
			port = Integer.parseInt(host.substring(next + 1));//jdk8
		}
		return true;
	}
}
