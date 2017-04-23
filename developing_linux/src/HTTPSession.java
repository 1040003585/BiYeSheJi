import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class HTTPSession implements Runnable {

	static long threadCount = 0;
	private Socket clientSocket = null;
	Thread t = null;
	// HTTP ���������ֽ��� 1024*8=2^13
	final static int bufsize = 8192;
	byte[] buf = new byte[bufsize];
	// Host��ʵ��������
	Host targethost = null;

	// ���ܵ��ͻ���Socket s����
	public HTTPSession(Socket s) {
		// TODO Auto-generated constructor stub
		clientSocket = s;
		// Ϊ�ôλỰ����һ��Daemon�߳�
		t = new Thread(this);
		t.setDaemon(true);
		t.start();
	}

	// @Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("\t[+] HTTPSession.run():StartID : " + t.getId());
		threadCount++;
		try {
			InputStream isInputStream = clientSocket.getInputStream();
			if (isInputStream == null)
				return;

			// �״ζ�bufsize��С��isInputStreamд��buf
			int readll = GetHeaderToBuf(isInputStream, bufsize, buf, 0);

			// ������ȡͷ������
			ByteArrayInputStream bais = new ByteArrayInputStream(buf, 0, readll);
			InputStreamReader isr = new InputStreamReader(bais);
			BufferedReader br = new BufferedReader(isr);

			// ������ͷ����ȡ����
			targethost = new Host();
			ReadHeaderData(br, targethost);

			// ����������Ϣ�������IP��ַ�Ͷ˿ں�
			targethost.cal();
			System.out.println("\t\t[+] Address:[" + targethost.address
					+ "]Port:" + targethost.port);

			// �ͻ�������������ת�ܵ�
			System.out.println("\t\t[+] Pipe Start: -----------------");
			try {
				Pipe(buf, readll, clientSocket.getInputStream(),
						clientSocket.getOutputStream(), targethost);
			} catch (Exception e) {
				System.out.println("\t\t[#] Pipe Exception!" + e.toString());
				// e.printStackTrace();// print red color
			}
			System.out.println("\t\t[-] Address:[" + targethost.address
					+ "]Port:" + targethost.port);
			System.out.println("\t\t[-] Pipe End  : -----------------");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("\t[-] HTTPSession.run():End ID : " + t.getId());
		System.out.println("\t[*] ThreadCount:" + --threadCount);
	}// run()

	/**
	 * // ������ͷ����ȡ����
	 * 
	 * @param br
	 * @param targethost
	 * @throws IOException
	 */
	private void ReadHeaderData(BufferedReader br, Host targethost)
			throws IOException {
		String headdataline = null;
		boolean flag = false;
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
			clientSocket.getOutputStream().write("error!".getBytes());
			clientSocket.close();
			System.out.println("\t\t[#] No host of head data");
			return;
		}
	}

	/**
	 * // �״ζ�bufsize��С��isInputStreamд��buf
	 * 
	 * @param isInputStream
	 * @param bufsize
	 * @param buf
	 * @param readll
	 * @return
	 * @throws IOException
	 */
	private int GetHeaderToBuf(InputStream isInputStream, final int bufsize,
			byte[] buf, int readll) throws IOException {
		int splitheadbyte = 0;
		int readl = isInputStream.read(buf, 0, bufsize);
		while (readl > 0) {
			readll += readl;
			/* ��Http����ͷ�Ľ���λ�� */
			splitheadbyte = FindHeaderEnd(buf, readll);
			if (splitheadbyte > 0) {
				// break while ��ʾ�ҵ�����ͷ����λ��
				break;
			}
			// ��bufʣ�³���bufsize - readll
			readl = isInputStream.read(buf, readll, bufsize - readll);
			System.out.println("\t\t[*] ��bufsizeʣ�³���bufsize-havereadlen");
		}// while
		return readll;

	}

	/**
	 * ��Http����ͷ�Ľ���λ��
	 * 
	 * @param buf
	 * @param readll
	 * @return
	 */
	private int FindHeaderEnd(final byte[] buf, int readll) {
		int splitbyte = 0;
		while (splitbyte + 3 < readll) {
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
	 * // �ͻ�������������ת�ܵ�
	 * 
	 * @param requesthead
	 * @param requestLen
	 * @param clientIS
	 * @param clientOS
	 * @param targethost
	 * @throws IOException
	 */
	void Pipe(byte[] requesthead, int requestLen, InputStream clientIS,
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
					System.out.println("\t\t\t[#] Target Socket Exception: "
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
