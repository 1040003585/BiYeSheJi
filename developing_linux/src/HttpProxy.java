import java.io.IOException;
import java.net.ServerSocket;

public class HttpProxy {

	private ServerSocket myServerSocket; // ������Socket
	private Thread myThread; // ����Daemon�߳�

	public HttpProxy(int port) throws IOException {
		myServerSocket = new ServerSocket(port);
		myThread = new Thread(new Runnable() {

			//@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					try {
						System.out.println("[+] HttpProxy.HttpProxy():StartID: "
								+ myThread.getId());
						// �ȴ�HTTP�Ự���ӣ������ͻ���Socket����
						new HTTPSession(myServerSocket.accept());
						System.out.println("[-] HttpProxy.HttpProxy():End ID : "
								+ myThread.getId());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		});
		// ���������߳�ΪDaemon�̣߳���������
		myThread.setDaemon(true);
		myThread.start();
	}

	/**
	 * ���������������
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new HttpProxy(4444); // ��������������Ķ˿�
		} catch (IOException e1) {
			System.err.println("Couldn't start server:\n" + e1);
			System.exit(-1);
		}
		System.out.println("Start proxy...(Get return to Stop proxy!)");

		// ����س�ֹͣ�������
		try {
			System.in.read();
		} catch (IOException e) {
			System.out.println("Stop proxy...");
		}
	}

}
