import java.net.*;
import java.io.*;
import javax.net.ssl.*;
import java.security.KeyStore;

public class ChatClient implements Runnable
{  //private Socket socket              = null;
   private SSLSocket socket = null;
   private volatile Thread thread     = null;
   private BufferedReader   console   = null;
   private DataOutputStream streamOut = null;
   private ChatClientThread client    = null;
   private String CLIENT_KEY_STORE_PASSWORD = "111111";
   private String CLIENT_TRUST_KEY_STORE_PASSWORD = "111111";
   private String username = "";

   public ChatClient(String serverName, int serverPort, String username)
   {  
      this.username = username;
      create(serverName, serverPort);
   }
   
   public ChatClient(String serverName, int serverPort){
      create(serverName, serverPort);
   }
   
   private void create(String serverName, int serverPort) {
      System.out.println("Establishing connection. Please wait ...");
         try
         {  //socket = new Socket(serverName, serverPort);

         SSLContext ctx = SSLContext.getInstance("SSL");
         KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
         TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
         KeyStore ks = KeyStore.getInstance("JKS");
         KeyStore tks = KeyStore.getInstance("JKS");
         ks.load(new FileInputStream("myCliKeystore"), CLIENT_KEY_STORE_PASSWORD.toCharArray());
         tks.load(new FileInputStream("myCliTruststore"), CLIENT_TRUST_KEY_STORE_PASSWORD.toCharArray());
         kmf.init(ks, CLIENT_KEY_STORE_PASSWORD.toCharArray());
         tmf.init(tks);
          

         ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) sslsocketfactory.createSocket(serverName, serverPort);
            

            System.out.println("Connected: " + socket);

            start();
         }
         catch(UnknownHostException uhe)
         {  System.out.println("Host unknown: " + uhe.getMessage()); }
         catch(IOException ioe)
         {  System.out.println("Unexpected exception: " + ioe.getMessage()); }
         catch(Exception e){
            
         }
   }
   
   public void run()
   {  Thread thisThread = Thread.currentThread();

      try {
         streamOut.writeUTF(username +  "#");
         streamOut.flush();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         System.out.println("error");
         //e.printStackTrace();
      }

      while (thread == thisThread)
      while (thread != null)
      {  try
         {  streamOut.writeUTF(console.readLine());
            streamOut.flush();
         }
         catch(IOException ioe)
         {  System.out.println("Sending error: " + ioe.getMessage());
            stop();
         }
      }
   }
   public void handle(String msg)
   {  if (msg.equals(".bye"))
      {  System.out.println("Good bye. Press RETURN to exit ...");
         stop();
      }
      else
         System.out.println(msg);
   }
   public void start() throws IOException
   {  console   = new BufferedReader(new InputStreamReader(System.in));
      streamOut = new DataOutputStream(socket.getOutputStream());
      if (thread == null)
      {  client = new ChatClientThread(this, socket);
         thread = new Thread(this);                   
         thread.start();
      }
   }
   public void stop()
   {  if (thread != null)
      {  thread = null;
      }
      try
      {  if (console   != null)  console.close();
         if (streamOut != null)  streamOut.close();
         if (socket    != null)  socket.close();
      }
      catch(IOException ioe)
      {  System.out.println("Error closing ..."); }
      client.close();  
      client.stopThread();
   }
   public static void main(String args[])
   {  ChatClient client = null;
      if (args.length != 3)
         System.out.println("Usage: java ChatClient host port username");
      else
         client = new ChatClient(args[0], Integer.parseInt(args[1]), args[2]);
   }
}