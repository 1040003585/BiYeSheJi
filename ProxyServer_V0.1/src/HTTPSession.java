import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class HTTPSession implements Runnable {

	static long threadCount = 0;
	private Socket clientSocket;
	Thread t = null;

	// ���ܵ��ͻ���Socket s����
	public HTTPSession(Socket s) {
		// TODO Auto-generated constructor stub
		clientSocket = s;
		// Ϊ�ôλỰ����һ��Daemon�߳�
		t = new Thread(this);
		t.setDaemon(true);
		t.start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("\t[+] HTTPSession.run():StartID : " + t.getId());
		threadCount++;
		try {
			InputStream isInputStream = clientSocket.getInputStream();
			if (isInputStream == null)
				return;
			final int bufsize = 8192;// http���������ֽ��� 1024*8=2^13
			byte[] buf = new byte[bufsize];
			int splitheadbyte = 0;
			int havereadlen = 0;
			{// region

				// �״ζ�bufsize��С��isInputStreamд��buf
				int readlen = isInputStream.read(buf, 0, bufsize);
				while (readlen > 0) {
					havereadlen += readlen;
					/* ��Http����ͷ�Ľ���λ�� */
					splitheadbyte = findHeaderEnd(buf, havereadlen);
					if (splitheadbyte > 0) {
						// break while ��ʾ�ҵ�����ͷ����λ��
						break;
					}
					// ��bufsizeʣ�³���bufsize-havereadlen
					readlen = isInputStream.read(buf, havereadlen, bufsize
							- havereadlen);
					System.out
							.println("\t\t[*] ��bufsizeʣ�³���bufsize-havereadlen");
				}// while

				ByteArrayInputStream bais = new ByteArrayInputStream(buf, 0,
						havereadlen);
				InputStreamReader isr = new InputStreamReader(bais);
				BufferedReader br = new BufferedReader(isr);

				/* Host��ʵ�������� */
				Host targethost = new Host();
				{// Host
					String headdataline = null;
					boolean flag = false;
					// ������ͷ����ȡ����
					while ((headdataline = br.readLine()) != null) {
						if (headdataline.toLowerCase().startsWith("host:")) {
							targethost.host = headdataline;
							flag = true;
						}
						// �������ͷ��Ϣ��data1.txt
						System.out.println("\t\t[*] " + headdataline);
					}// while
					// ����ͷ�����������û��Host��Ϣ
					if (!flag) {
						clientSocket.getOutputStream().write(
								"error!".getBytes());
						clientSocket.close();
						System.out.println("\t\t[#] No host of head data");
						return;
					}
					// ����������Ϣ�������IP��ַ�Ͷ˿ں�
					targethost.cal();
					System.out.println("\t\t[+] Address:[" + targethost.address
							+ "]Port:" + targethost.port);
					System.out.println("\t\t[+] Pipe Start: -----------------");
					// �ͻ�������������ת�ܵ�
					try {
						pipe(buf, havereadlen, clientSocket.getInputStream(),
								clientSocket.getOutputStream(), targethost);
					} catch (Exception e) {
						System.out.println("\t\t[#] Pipe Run Exception!"
								+ e.toString());
						// e.printStackTrace();// print red color
					}
					System.out.println("\t\t[-] Address:[" + targethost.address
							+ "]Port:" + targethost.port);
					System.out.println("\t\t[-] Pipe End  : -----------------");
				}// Host
			}// region

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("\t[-] HTTPSession.run():End ID : " + t.getId());
		System.out.println("\t[*] ThreadCount:" + --threadCount);
	}// run()

	/**
	 * ��Http����ͷ�Ľ���λ��
	 **/
	private int findHeaderEnd(final byte[] buf, int rlen) {
		int splitbyte = 0;
		while (splitbyte + 3 < rlen) {
			if (buf[splitbyte] == '\r' && buf[splitbyte + 1] == '\n'
					&& buf[splitbyte + 2] == '\r' && buf[splitbyte + 3] == '\n') {
				// �����ײ��뱨��������һ�����У�CR+LF��
				return splitbyte + 4;
			}

			splitbyte++;
		}
		return 0;
	}

	/**
	 * �ͻ�������������ת�ܵ�
	 * 
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	void pipe(byte[] requesthead, int requestLen, InputStream clientIS,
			OutputStream clientOS, Host targethost) throws IOException {
		byte bytes[] = new byte[1024 * 32];
		// ����Ŀ��Socket��Ŀ���������Ŀ��������
		Socket targetsocket = new Socket(targethost.address, targethost.port);
		targetsocket.setSoTimeout(3000);
		OutputStream targetOS = targetsocket.getOutputStream();
		InputStream targetIS = targetsocket.getInputStream();
		try {
			do {
				System.out
						.println("\t\t\t[+] Proxy requset-connect Start , Target Socket: "
								+ targetsocket.hashCode());
				// ��Ŀ��Socket�������д������ͷ
				targetOS.write(requesthead, 0, requestLen);
				int resultLen = 0;
				try {
					while ((resultLen = targetIS.read(bytes)) != -1
							&& !clientSocket.isClosed()
							&& !targetsocket.isClosed()) {
						// ����Ŀ�������������ͻ���Socketд��
						clientOS.write(bytes, 0, resultLen);
					}
				} catch (Exception e) {
					System.out.println("\t\t\t[#] Target Socket exception:"
							+ e.toString());
				}
				System.out
						.println("\t\t\t[-] Proxy requset-connect Broken, Target Socket: "
								+ targetsocket.hashCode());
			} while (!clientSocket.isClosed()
					&& (requestLen = clientIS.read(requesthead)) != -1);
		} catch (Exception e) {
			System.out.println("\t\t\t[#] Client Socket exception:"
					+ e.toString());
		}
		System.out.println("\t\t\t[-] End, Target Socket: "
				+ targetsocket.hashCode());
		targetOS.close();
		targetIS.close();
		clientIS.close();
		clientOS.close();
		targetsocket.close();
		clientSocket.close();
	}

}