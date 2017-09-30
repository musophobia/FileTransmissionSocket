/**
 * Created by USER on 9/27/2017.
 */
import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Server {

    // The server socket.
    private static ServerSocket serverSocket = null;
    // The client socket.
    private static Socket clientSocket = null;
    private DataInputStream dis = null;
    private DataOutputStream oOs= null;
    private InputStream is= null;
    private OutputStream os = null;
    // This chat server can accept up to maxClientsCount clients' connections.
    private static final int maxClientsCount = 100;
    private static final clientThread[] threads = new clientThread[maxClientsCount];
    private static final Map<String,clientThread> studentMap = new ConcurrentHashMap<String,clientThread>();
    private static byte[] mybytearray=null;
    public static void main(String args[]) {


        int portNumber = 2222;
        if (args.length < 1) {
            System.out
                    .println("Usage: java MultiThreadChatServer <portNumber>\n"
                            + "Now using port number=" + portNumber);
        } else {
            portNumber = Integer.valueOf(args[0]).intValue();
        }


        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println(e);
        }


        while (true) {
            try {
                clientSocket = serverSocket.accept();
                int i = 0;
                for (i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == null) {
                        (threads[i] = new clientThread(clientSocket, threads, studentMap)).start();
                       // DataOutputStream os = new DataOutputStream(clientSocket.getOutputStream());
                      //  os.writeBytes("LeftOut");
                        break;
                    }
                }

                if (i == maxClientsCount) {
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println("Server too busy. Try later.");
                    os.close();
                    clientSocket.close();
                }
            //    while(true){
                    //file receive end/
                /*
                    ObjectInputStream oIs=new ObjectInputStream(clientSocket.getInputStream());


                    int current=0;
                    int bytesRead;
                    String filePath = new File("").getAbsolutePath()+"\\a.pdf";
                    FileOutputStream fos = new FileOutputStream(filePath);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    mybytearray=new byte[6535555];
                    System.out.println("hehe");
                    bytesRead = oIs.read(mybytearray,0,mybytearray.length);
                         System.out.println(bytesRead);
                     current = bytesRead;

                    do {
                        bytesRead =
                                oIs.read(mybytearray, current, (mybytearray.length-current));
                        if(bytesRead >= 0) current += bytesRead;
                    } while(bytesRead > -1);

                    bos.write(mybytearray, 0 , mybytearray.length);
                    bos.flush();
                    DataOutputStream oOs= new DataOutputStream(clientSocket.getOutputStream());
                    System.out.println("DOne");
                    oOs.writeBytes("YES");
*/
              //  }

            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}