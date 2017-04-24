import java.io.IOException;
import java.net.ServerSocket;

public class HttpProxy {

	private ServerSocket myServerSocket; // 服务器Socket
	private Thread myThread; // 链接Daemon线程

	public HttpProxy(int port) throws IOException {
		myServerSocket = new ServerSocket(port);
		myThread = new Thread(new Runnable() {

			// @Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					try {
						System.out
								.println("[+] HttpProxy.HttpProxy():StartID: "
										+ myThread.getId());
						// 等待HTTP会话链接，建立客户端Socket连接
						new HTTPSession(myServerSocket.accept());
						System.out
								.println("[-] HttpProxy.HttpProxy():End ID : "
										+ myThread.getId());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		});
		// 设置链接线程为Daemon线程，并启动。
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
			new HttpProxy(4444); // 启动代理服务器的端口
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