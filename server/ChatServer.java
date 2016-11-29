import java.net.*;
import java.io.*;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.util.Hashtable;
public class ChatServer implements Runnable {
   
   private ChatServerThread clients[] = new ChatServerThread[50];
   //private ServerSocket server = null;
   private SSLServerSocket sslserver;
   private volatile Thread  thread = null;
   private int clientCount = 0;
   private Hashtable<String,Integer > clientIPs =  new Hashtable<String,Integer >();
   private int maxConnect = -1;

   public ChatServer(int port, int limit) {  
      this.maxConnect = limit;
      
      try {
         
         System.out.println("Binding to port " + port + ", please wait  ...");
         //server = new ServerSocket(port); 
         
         SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
         sslserver = (SSLServerSocket) sslserversocketfactory.createServerSocket(port);

         System.out.println("Server started: " + sslserver);
         start();
      } catch(IOException ioe) {
         System.out.println("Can not bind to port " + port + ": " + ioe.getMessage());
      }
   }
   public void run()
   {  Thread thisThread = Thread.currentThread();
      while (thread == thisThread)
      {  try
         {  System.out.println("Waiting for a client ..."); 
            addThread((SSLSocket) sslserver.accept()); }
         catch(IOException ioe)
         {  System.out.println("Server accept error: " + ioe); stop(); }
      }
   }
   public void start()
   {  if (thread == null)
      {  thread = new Thread(this); 
         thread.start();
      }
   }
   public void stop()
   {  if (thread != null)
      {  thread = null;
      }
   }
   private int findClient(long iD)
   {  for (int i = 0; i < clientCount; i++)
         if (clients[i].getID() == iD)
            return i;
      return -1;
   }
   public synchronized void handle(long iD, String input)
   {  
      ChatServerThread targetClient = clients[findClient(iD)];
      if (targetClient.username.equals("")) {
         targetClient.username = input + iD;
      }
      else if (input.equals(".bye"))
      {  targetClient.send(".bye");
         remove(iD); }
      else
         for (int i = 0; i < clientCount; i++)
            clients[i].send(targetClient.username + ": " + input);   
   }
   public synchronized void remove(long iD)
   {  int pos = findClient(iD);
      if (pos >= 0)
      {  ChatServerThread toTerminate = clients[pos];
         System.out.println("Removing client thread " + iD + " at " + pos);
         //remove IP form clientIPs
         String ip = toTerminate.getIP();
         int ipCount = clientIPs.get(ip);
         if(ipCount==0){
             clientIPs.remove(ip);
         }else{
            ipCount--;
            clientIPs.put(ip,ipCount);
         }

         if (pos < clientCount-1)
            for (int i = pos+1; i < clientCount; i++)
               clients[i-1] = clients[i];
         clientCount--;
         try
         {  toTerminate.close(); }
         catch(IOException ioe)
         {  System.out.println("Error closing thread: " + ioe); }
         toTerminate.stopThread(); 
      }
   }
   private void addThread(Socket socket)
   {  if (clientCount < clients.length)
      {  
         //checking connect
         String ip = socket.getInetAddress().toString();
         int ipCount =0;
         if(clientIPs.get(ip)!=null){
            ipCount = clientIPs.get(ip);
         }
         ipCount++;
         System.out.println("IP: " + ip + " count :"+ipCount);
         if(ipCount>maxConnect){
            System.out.println("Client refused: maximum connection per client :" + maxConnect);
             try
            {  socket.close();
               ipCount--; } //client will error ????
            catch(IOException ioe)
            {  System.out.println("Error closing thread: " + ioe); }; 
            return;
         }
         clientIPs.put(ip,ipCount);
         //end checking connect

         System.out.println("Client accepted: " + socket);

         clients[clientCount] = new ChatServerThread(this, socket);

         try
         {  clients[clientCount].open(); 
            clients[clientCount].start();  
            clientCount++; }
         catch(IOException ioe)
         {  System.out.println("Error opening thread: " + ioe); } }
      else
         System.out.println("Client refused: maximum " + clients.length + " reached.");
   }
   public static void main(String args[])
   {  ChatServer server = null;
      if (args.length != 2)
         System.out.println("Usage: java ChatServer port limit");
      else
         server = new ChatServer(Integer.parseInt(args[0]),Integer.parseInt(args[1]));
   }
}
