public class Main {

	public static String address;
	public static int port;
	// public static String host = "Host:127.0.0.1:8080";
	// public static String host = "Host: 127.0.0.1:8080";
	public static String host = "Host: github.com";

	static public boolean cal() {
		if (host == null)
			return false;
		int start = host.indexOf(": ");
		if (start == -1)
			return false;
		int next = host.indexOf(':', start + 2);
		if (next == -1) {
			port = 80;
			address = host.substring(start + 2);
		} else {
			address = host.substring(start + 2, next);
			port = Integer.valueOf(host.substring(next + 1));
		}
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		cal();
		System.out.println(address);
		System.out.println(port);
	}

}
