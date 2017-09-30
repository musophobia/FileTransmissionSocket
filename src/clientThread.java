import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by USER on 9/28/2017.
 */

class clientThread extends Thread {


    private static int MaxBuf=10000;
    private static int chunkSize=100;
    private int chunks=0;
    private String toReceive="";
    private ObjectInputStream is = null;
    private ObjectOutputStream os = null;
    private Socket clientSocket = null;
    private final clientThread[] threads;
    private int maxClientsCount;
    private Map<String,clientThread> studentMap=new ConcurrentHashMap<>();
    private String name = null;
    private String line = null;
    private static boolean isReading = false;
    int bytesRead;
    int current = 0;
    FileOutputStream fos = null;
    FileInputStream fis = null;
    ObjectOutputStream osF = null;
    BufferedInputStream bis = null;
    BufferedOutputStream bos = null;
    private DataOutputStream dos = null;
    private static int fileID=0;
    private byte[][][] arrayO=new byte[100][][];
    private byte[][] getMybytearray=null;
    private byte[] mybytearray = new byte[100];

    public clientThread(Socket clientSocket, clientThread[] threads, Map<String,clientThread> studentMap,byte[][][]mybyte) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
        this.studentMap = studentMap;
        this.arrayO=mybyte;
    }

    public void run() {
        int maxClientsCount = this.maxClientsCount;
        clientThread[] threads = this.threads;
    //    Map<Integer, clientThread> studentMap=this.studentMap;

        try {
      /*
       * Create input and output streams for this client.
       */

            is = new ObjectInputStream(clientSocket.getInputStream());
            os = new ObjectOutputStream(clientSocket.getOutputStream());
            String filePath = new File("").getAbsolutePath()+"\\a.JPG";
            fos = new FileOutputStream(filePath);
            bos = new BufferedOutputStream(fos);
          //  dos = new DataOutputStream(clientSocket.getOutputStream());
          //  osF = new ObjectOutputStream(clientSocket.getOutputStream());
         //   ObjectInputStream isF = new ObjectInputStream(clientSocket.getInputStream());
            int cou=0;
          while(cou<100) {
              int i=0;
              os.writeObject("Enter Student ID: ");
             // name = is.readLine().trim();
              try {
                  name=(String)is.readObject();
                  //eikhane map korbo student ID'r accord e
                  for (i = 0; i < maxClientsCount; i++) {
                      if (threads[i] == this && studentMap.get(name) == null) {
                          studentMap.put(name, threads[i]);
                        //  threads[i].os.writeObject("Student ID " + name + " online now");
                          os.writeObject("Student ID " + name + " login done. Write \" logout \" to logout");
                          //eita diye sob thread e khobor pathabo je ekjon online hoise.. yeyeyeye
                      /*    for (int j = 0; j < maxClientsCount; j++) {
                              if (threads[j] != null && threads[j] != this) {
                                  threads[j].os.writeObject("Student ID " + name + " online now");
                              }
                          }  */
                          System.out.println("hi hi");
                          break;
                      } else if (threads[i] == this && studentMap.get(name) != null) {
                          threads[i].os.writeObject("Student ID already logged in");
                      }
                  }
              } catch (ClassNotFoundException e) {
                  e.printStackTrace();
              }

          //    System.out.println("ki ki ki");
              //mapping done
              if(i<maxClientsCount) cou=10000;
              cou++;
          }

/* **file to be sent here now on..and using the isReading things :p */
        while (true) {
            if(isReading==false) {
                try {
                    line="";
                    line = (String) is.readObject();
                    System.out.println(line);
                    if (line.startsWith("logout")) {
                        os.writeObject("log out");
                        break;
                    }
                    else if(line.contains("\\")){
                        String [] tempStr=line.split(" ");
                        File file = new File(tempStr[0]);
                        toReceive=tempStr[1];
                        System.out.println((int) file.length());
                        System.out.println(toReceive);
                        mybytearray = new byte[(int)file.length()];
                        chunks=(int)file.length()/100;
                        System.out.println(chunks);
                        if ((((int) file.length()+99) / 100) > MaxBuf) {
                            os.writeObject("overflowed");
                        }
                        else {
                            os.writeObject("ready" + chunks);
                            //eikhane array ta dite hobe
                            getMybytearray=new byte[chunks][];
                            isReading = true;
                            current=0;
                        }
                    }
                    else if(line.contains("hola")) {
                    }
                    else os.writeObject("nofilegiven");
                     //   System.out.println(line);
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            else if(isReading==true){
                //read korte thakbe :3
                synchronized (is) {
                    mybytearray = (byte[]) is.readObject();
                }
                System.out.println(mybytearray);
                getMybytearray[current]=mybytearray;
                current+=1;
                if(current>=chunks) {
                    isReading = false;
                    if(studentMap.get(toReceive)==null){
                        os.writeObject("Not available user now");
                        continue;
                    }
                    System.out.println(toReceive);
                    for (int i = 0; i < maxClientsCount; i++) {
                        if (threads[i] == studentMap.get(toReceive)) {
                            System.out.println(Integer.toString(fileID)+" "+name+" "+Integer.toString(getMybytearray.length*getMybytearray[0].length));
                            threads[i].os.writeObject(Integer.toString(fileID)+" "+name+" "+Integer.toString(getMybytearray.length*getMybytearray[0].length));
                        }
                    }
                    os.writeObject(Integer.toString(getMybytearray.length*getMybytearray[0].length));
                    arrayO[fileID]=getMybytearray;
                    fileID++;
                    System.out.println("y");
                    continue;
                }
                else {
                    os.writeObject("read some");
                }
            }

        }
        for (int i = 0; i < maxClientsCount; i++) {
            if (threads[i] == this) {
                threads[i] = null;
            }
            studentMap.remove(name);
        }
        is.close();
        os.close();
        clientSocket.close();
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}