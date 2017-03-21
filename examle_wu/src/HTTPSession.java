import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class HTTPSession implements Runnable {

	static long threadCount = 0;
	private Socket mySocket;

	public HTTPSession(Socket s) {
		// TODO Auto-generated constructor stub
		mySocket = s;
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("HTTPSession.run()");
		threadCount++;
		try {
			InputStream isInputStream = mySocket.getInputStream();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("ThreadCount:" + --threadCount);
	}

}
