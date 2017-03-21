import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
public class HttpProxy {
 // 统计连接数量
 static long threadCount = 0;

 int myTcpPort = 8080;
 private ServerSocket myServerSocket;
 private Thread myThread;
 public HttpProxy(int port) throws IOException {
  myTcpPort = port;
  // 创建服务端套接字，监听8888端口
  myServerSocket = new ServerSocket(myTcpPort);
  
  // 开启一个守护线程，也就是所谓的服务线程，处理客户端连接
  myThread = new Thread(new Runnable() {
   public void run() {
    try {
	 // 无限循环
     while (true)
	  // 每当客户端发起连接请求，新建一个会话（线程）专门处理与该客户端的交互
      new HTTPSession(myServerSocket.accept());
    } catch (IOException ioe) {
    }
   }
  });
  myThread.setDaemon(true);
  myThread.start();
 }
 /**
  * Stops the server.
  */
 public void stop() {
  try {
   // 关闭服务端套接字，此时new HTTPSession(myServerSocket.accept());会抛出异常，跳出无限循环，myThread线程执行结束
   myServerSocket.close();
   // 停止执行其他线程，直到myThread线程执行结束
   myThread.join();
  } catch (IOException ioe) {
  } catch (InterruptedException e) {
  }
 }
 public class HTTPSession implements Runnable {
  private Socket mySocket;
  public HTTPSession(Socket s) {
   mySocket = s;
   // 守护线程，处理客户端交互
   Thread t = new Thread(this);
   t.setDaemon(true);
   t.start();
  }
  @Override
  public void run() {
   try {
    ++threadCount;
    InputStream is = mySocket.getInputStream();
    if (is == null)
     return;
    final int bufsize = 8192;
    byte[] buf = new byte[bufsize];
    int splitbyte = 0;
	// 读取长度
    int rlen = 0;
    {
     int read = is.read(buf, 0, bufsize);
     while (read > 0) {
      rlen += read;
	  
	  // 查询请求头结束位置
      splitbyte = findHeaderEnd(buf, rlen);
	  // 如果splitbyte大于0，表明找到请求头的结束位置，则停止读取客户端输入
      if (splitbyte > 0)
       break;
      read = is.read(buf, rlen, bufsize - rlen);
     }
     ByteArrayInputStream hbis = new ByteArrayInputStream(buf,
       0, rlen);
     BufferedReader hin = new BufferedReader(
       new InputStreamReader(hbis));
     Host host = new Host();
     {
      String string;
      boolean flag = false;
      while ((string = hin.readLine()) != null) {
       if (string.toLowerCase().startsWith("host:")) {
        host.host = string;
        flag = true;
       }
       System.out.println(string);
      }
      if (!flag) {
       mySocket.getOutputStream().write(
         "error!".getBytes());
       mySocket.close();
       return;
      }
     }
	 // 根据主机信息，计算出ip地址和端口号
     host.cal();
     System.out.println("address:[" + host.address + "]port:"
       + host.port + "\n-------------------\n");
     try {
	  // 根据计算出来的ip地址和端口号，连接并发送请求
      pipe(buf, rlen, mySocket, mySocket.getInputStream(),
        mySocket.getOutputStream(), host);
     } catch (Exception e) {
      System.out.println("Run Exception!");
      e.printStackTrace();
     }
    }
   } catch (Exception e) {
   }
   System.out.println("threadcount:" + --threadCount);
  }
  
  /**
   * 找Http请求头的结束位置，
   * find http header
   **/
  private int findHeaderEnd(final byte[] buf, int rlen) {
   int splitbyte = 0;
   while (splitbyte + 3 < rlen) {
    if (buf[splitbyte] == '\r' && buf[splitbyte + 1] == '\n' && buf[splitbyte + 2] == '\r' && buf[splitbyte + 3] == '\n')
     return splitbyte + 4;
    splitbyte++;
   }
   return 0;
  }
  
  void pipe(byte[] request, int requestLen, Socket client,
    InputStream clientIS, OutputStream clientOS, Host host)
    throws Exception {
   byte bytes[] = new byte[1024 * 32];
   Socket socket = new Socket(host.address, host.port);
   socket.setSoTimeout(3000);
   OutputStream os = socket.getOutputStream();
   InputStream is = socket.getInputStream();
   try {
    do {
     os.write(request, 0, requestLen);
     int resultLen = 0;
     try {
      while ((resultLen = is.read(bytes)) != -1
        && !mySocket.isClosed() && !socket.isClosed()) {
       clientOS.write(bytes, 0, resultLen);
      }
     } catch (Exception e) {
      System.out.println("target Socket exception:"
        + e.toString());
     }
     System.out.println("proxy requset-connect broken,socket:"
       + socket.hashCode());
    } while (!mySocket.isClosed()
      && (requestLen = clientIS.read(request)) != -1);
   } catch (Exception e) {
    System.out.println("client Socket exception:" + e.toString());
   }
   System.out.println("end,socket:" + socket.hashCode());
   os.close();
   is.close();
   clientIS.close();
   clientOS.close();
   socket.close();
   mySocket.close();
  }
  
  // 目标主机信息
  // target Host info
  final class Host {
   public String address;
   public int port;
   public String host;
   public boolean cal() {
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
  }
 }
 public static void main(String[] args) {
  try {
	// 创建代理服务器，监听8888端口
   new HttpProxy(8888);
  } catch (IOException ioe) {
   System.err.println("Couldn't start server:\n" + ioe);
   System.exit(-1);
  }
  System.out.println("start!");
  try {
	// 阻塞
   System.in.read();
  } catch (Throwable t) {
  }
  System.out.println("stop!");
 }
}