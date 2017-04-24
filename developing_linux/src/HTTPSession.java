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
	// HTTP 请求的最大字节数 1024*8=2^13
	final static int bufsize = 8192;
	byte[] buf = new byte[bufsize];
	// Host类实例化对象
	Host targethost = null;

	// 接受到客户端Socket s连接
	public HTTPSession(Socket s) {
		// TODO Auto-generated constructor stub
		clientSocket = s;
		// 为该次会话建立一个Daemon线程
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

			// 首次读bufsize大小的isInputStream写到buf
			int readll = GetHeaderToBuf(isInputStream, bufsize, buf, 0);

			// 构建读取头输入流
			ByteArrayInputStream bais = new ByteArrayInputStream(buf, 0, readll);
			InputStreamReader isr = new InputStreamReader(bais);
			BufferedReader br = new BufferedReader(isr);

			// 从请求头流读取数据
			targethost = new Host();
			ReadHeaderData(br, targethost);

			// 根据主机信息，计算出IP地址和端口号
			targethost.cal();
			System.out.println("\t\t[+] Address:[" + targethost.address
					+ "]Port:" + targethost.port);

			// 客户端请求链接中转管道
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
	 * // 从请求头流读取数据
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
			// 输出请求头信息如data1.txt
			System.out.println("\t\t[*] " + headdataline);
		}// while
			// 请求头的流数据如果没有Host信息
		if (!flag) {
			clientSocket.getOutputStream().write("error!".getBytes());
			clientSocket.close();
			System.out.println("\t\t[#] No host of head data");
			return;
		}
	}

	/**
	 * // 首次读bufsize大小的isInputStream写到buf
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
			/* 找Http请求头的结束位置 */
			splitheadbyte = FindHeaderEnd(buf, readll);
			if (splitheadbyte > 0) {
				// break while 表示找到请求头结束位置
				break;
			}
			// 读buf剩下长度bufsize - readll
			readl = isInputStream.read(buf, readll, bufsize - readll);
			System.out.println("\t\t[*] 读bufsize剩下长度bufsize-havereadlen");
		}// while
		return readll;

	}

	/**
	 * 找Http请求头的结束位置
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
				// 报文首部与报文主体有一个空行（CR+LF）
				return splitbyte + 4;
			}
			splitbyte++;
		}
		return 0;
	}

	/**
	 * // 客户端请求链接中转管道
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
		// 建立目标Socket和目标输出流和目标输入流
		Socket targetsocket = new Socket(targethost.address, targethost.port);
		targetsocket.setSoTimeout(3000);
		OutputStream targetOS = targetsocket.getOutputStream();
		InputStream targetIS = targetsocket.getInputStream();
		try {
			do {
				System.out
						.println("\t\t\t[+] Proxy requset-connect Start , Target Socket: "
								+ targetsocket.hashCode());
				// 向目标Socket的输出流写入请求头
				targetOS.write(requesthead, 0, requestLen);
				int resultLen = 0;
				try {
					while ((resultLen = targetIS.read(bytes)) != -1
							&& !clientSocket.isClosed()
							&& !targetsocket.isClosed()) {
						// 请求到目标的正文向输出客户端Socket写入
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