import java.net.*;
import java.io.*;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.sql.Timestamp;

public class ChatServerThread extends Thread
{  private ChatServer       server    = null;
   private Socket           socket    = null;
   private long              ID        = -1;
   private DataInputStream  streamIn  =  null;
   private DataOutputStream streamOut = null;
   private volatile Thread  thread    = null;
   public String username = "";
   private String IP = "";
   private Timestamp activeTime ;

   public ChatServerThread(ChatServer _server, Socket _socket)
   {  super();
      server = _server;
      socket = _socket;
      ID     = this.getId();
      IP     = socket.getInetAddress().toString();
      activeTime =  new Timestamp(System.currentTimeMillis());
   }
   public void send(String msg)
   {   try
       {  streamOut.writeUTF(msg);
          streamOut.flush();
       }
       catch(IOException ioe)
       {  System.out.println(ID + " ERROR sending: " + ioe.getMessage());
          server.remove(ID);
          stopThread();
       }
   }
   public long getID()
   {  return ID;
   }
    public String getIP()
   {  return IP;
   }
   public void run()
   {  System.out.println("Server Thread " + ID + " running.");
      Thread thisThread = Thread.currentThread();
      while (thread == thisThread)
      {  try
         {  
        //  System.out.println(ID + " lastactive - currentTime :" + (new Timestamp(System.currentTimeMillis()).getTime()-activeTime.getTime()));
          activeTime =  new Timestamp(System.currentTimeMillis());
          server.handle(ID, streamIn.readUTF());
         }
         catch(IOException ioe)
         {  System.out.println(ID + " ERROR reading: " + ioe.getMessage());
            server.remove(ID);
            stopThread();
         }
      }
   }
   public void open() throws IOException
   {  streamIn = new DataInputStream(new 
                        BufferedInputStream(socket.getInputStream()));
      streamOut = new DataOutputStream(new
                        BufferedOutputStream(socket.getOutputStream()));
   }
   public void close() throws IOException
   {  if (socket != null)    socket.close();
      if (streamIn != null)  streamIn.close();
      if (streamOut != null) streamOut.close();
   }
   public void start()
   {  thread = new Thread(this);
      thread.start();
   }
   public void stopThread()
   {  thread = null;
   }
}
