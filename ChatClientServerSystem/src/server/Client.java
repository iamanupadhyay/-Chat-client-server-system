package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import pojo.MessageInfo;

//this tread act as user or client having all property and functionality of client or user on server side
//for broadcasting it is responsible for join user 
public class Client extends Thread{
    
    public ClientHandler clientHandler;
    public Socket socketAccept;
    public int uid;
    public String clientUserName="";
    public String ipaddress="";
    
    public ObjectInputStream objectInputStream;
    public ObjectOutputStream objectOutputStream;
    boolean done=false;
    public Client(ClientHandler clientHandler, Socket socketAccept) {
        this.clientHandler = clientHandler;
        this.socketAccept = socketAccept;
        this.uid = socketAccept.getPort();
        this.ipaddress=socketAccept.getRemoteSocketAddress().toString();
    }
    
    //this is use to broadcasting message information from server to user or client
      public void sendMessage(MessageInfo messageInfo){
        try {
            objectOutputStream.writeObject(messageInfo);
            objectOutputStream.flush();
        } 
        catch (IOException ex) {
            System.out.println("Error:"+ex.getMessage());
        }
    }
//this is use to getting client or user id;
    public int getUid() {
        return uid;
    }
    
    //this thread override method use to handling all request or message information coming from client or user  
    @Override
    public void run() {
         while (!done){  
    	    try{  
                MessageInfo messageInfo = (MessageInfo) objectInputStream.readObject();
                
                //this is use to handle user request
    	    	clientHandler.clientHandle(uid, messageInfo);
            }
            catch(Exception ioe){  
            	System.out.println(uid + " ERROR reading: " + ioe.getMessage());
                clientHandler.removeClient(uid);
                stopThread();
            }
        }
    }
 
    // with the help of this message user join chat box on server side
    public void joinChat() throws IOException {  
        objectOutputStream = new ObjectOutputStream(socketAccept.getOutputStream());
        objectOutputStream.flush();
        objectInputStream = new ObjectInputStream(socketAccept.getInputStream());
    }
    
    
 // with the help of this message user close chat box on server side
    public void closeChat() throws IOException {  
    	if (socketAccept != null)    
            socketAccept.close();
        if (objectInputStream != null)  
            objectInputStream.close();
        if (objectOutputStream != null) 
            objectOutputStream.close();
    }
    
    // stop thread execution
    public void stopThread() {
        done = true;
        interrupt();
    }
    
      
}