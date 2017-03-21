import java.io.IOException;
import java.net.ServerSocket;

public class HttpProxy {

	private ServerSocket myServerSocket;
	private Thread myThread;

	public HttpProxy(int port) throws IOException {
		myServerSocket = new ServerSocket(port);
		myThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					try {
						new HTTPSession(myServerSocket.accept());
						System.out.println("HttpProxy.HttpProxy()");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		});
		myThread.setDaemon(true);
		myThread.start();
	}

	/**
	 * 代理服务器主函数
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new HttpProxy(8888);
		} catch (IOException e1) {
			System.err.println("Couldn't start server:\n" + e1);
			System.exit(-1);
		}
		System.out.println("Start proxy...(Get return to Stop proxy!)");

		// 输入回车停止代理服务
		try {
			System.in.read();
		} catch (IOException e) {
			System.out.println("Stop proxy...");
		}
	}

}
