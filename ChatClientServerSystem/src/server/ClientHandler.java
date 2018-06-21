package server;

import pojo.MessageInfo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

//this class is responsible for handling all request or message info or event coming from client 
public class ClientHandler implements Runnable{

    public Client client[];
    public ServerSocket serverSocket;
    public Thread clientThread;
    int noOfClients=0,portNumber=5000,totalClients=100;
    DataBaseHandler dataBaseHandler;

    public ClientHandler() {
        client=new Client[totalClients];
        dataBaseHandler=new DataBaseHandler("data.txt", null);
        try{  
	    serverSocket = new ServerSocket(portNumber);
            portNumber = serverSocket.getLocalPort();
	    System.out.println("Start Server with IP Address :" + InetAddress.getLocalHost() + ", PortNumber :" + serverSocket.getLocalPort());
	    startThread(); 
        }
	catch(IOException ioe){  
            System.out.println("Error:"+ioe.getMessage()); 
           
	}
    }
    
    //this is use to join or connect client thread with server
     @Override
    public void run() {
        while (clientThread != null){  
            try{  
		System.out.println("Waiting for a client ..."); 
                joinClient(serverSocket.accept());
	       
	    }
	    catch(Exception ioe){ 
               System.out.println("Error:"+ioe.getMessage()); 
	    }
        }
  
    }
     
     // using this method we start connected or join client for communicating with server
     public void startThread(){  
	    	if (clientThread == null){  
	            clientThread = new Thread(this); 
		    clientThread.start();
		}
    }
     // this method is use to getting client position which are added in  client array
    private int getClientPostion(int uid){  
	    	for (int i = 0; i < noOfClients; i++){
	        	if (client[i].getUid()== uid){
	                    return i;
	                }
		}
	    	return -1;
    }
   
   
   //this method is use to handling all request which are coming from clients and broadcasting message which are coming from other user
    public synchronized void clientHandle(int uid, MessageInfo messageInfo) {
        if(messageInfo.contentData.equals("exit"))
        {
            sendMessageToAll("logout", "server", messageInfo.senderUser);
            removeClient(uid);
        }
        else
        {
               if(messageInfo.messageType.equals("login"))
               {
                    if(getClient(messageInfo.senderUser) == null){
                        if(dataBaseHandler.loginMethod(messageInfo.senderUser, messageInfo.contentData)){
                            client[getClientPostion(uid)].clientUserName = messageInfo.senderUser;
                            client[getClientPostion(uid)].sendMessage(new MessageInfo("login", "server", "yes", messageInfo.senderUser));
                            sendMessageToAll("newuser", "server", messageInfo.senderUser+client[getClientPostion(uid)].ipaddress);
                            sendListOfClients(messageInfo.senderUser);
                        }
                        else{
                            client[getClientPostion(uid)].sendMessage(new MessageInfo("login", "server", "no",messageInfo.senderUser));
                            } 
                    }
                    else{
                        client[getClientPostion(uid)].sendMessage(new MessageInfo("login", "server", "no", messageInfo.senderUser));
                    }
               }
                else if(messageInfo.messageType.equals("newmessage"))
                {
                    if(messageInfo.recipientUser.equals("All")){
                        // sendMessageToAll("newmessage", messageInfo.senderUser, messageInfo.contentData);
          
                    }
                    else{
                        getClient(messageInfo.recipientUser).sendMessage(new MessageInfo(messageInfo.messageType, messageInfo.senderUser, messageInfo.contentData, messageInfo.recipientUser));
                        client[getClientPostion(uid)].sendMessage(new MessageInfo(messageInfo.messageType,  messageInfo.senderUser, messageInfo.contentData, messageInfo.recipientUser));
                    }
                }
               else if(messageInfo.messageType.equals("connect")){
                    client[getClientPostion(uid)].sendMessage(new MessageInfo("connect", "server", "yes", messageInfo.senderUser));
                }
                  else if(messageInfo.messageType.equals("signup")){
                    if(getClient(messageInfo.senderUser) == null){
                        if(!dataBaseHandler.isUserExists(messageInfo.senderUser)){
                            dataBaseHandler.signUpMethod(messageInfo.messageType,messageInfo.recipientUser,messageInfo.senderUser, messageInfo.contentData);
                            client[getClientPostion(uid)].clientUserName = messageInfo.senderUser;
                            client[getClientPostion(uid)].sendMessage(new MessageInfo("signup", "server", "yes", messageInfo.senderUser));
                            client[getClientPostion(uid)].sendMessage(new MessageInfo("login", "server", "yes", messageInfo.senderUser));
                            sendMessageToAll("newuser", "server", messageInfo.senderUser+client[getClientPostion(uid)].ipaddress);
          
                            sendListOfClients(messageInfo.senderUser);
                        }
                        else{
                            client[getClientPostion(uid)].sendMessage(new MessageInfo("signup", "server", "no", messageInfo.senderUser));
                        }
                    }
                    else{
                        client[getClientPostion(uid)].sendMessage(new MessageInfo("signup", "server", "no", messageInfo.senderUser));
                    }
                }
                 else if(messageInfo.messageType.equals("upload_req")){
                    if(messageInfo.recipientUser.equals("All")){
                        //client[getClientPostion(uid)].sendMessage(new MessageInfo("newmessage", "server", "Uploading to all ", messageInfo.senderUser));
                    	for(int i = 0; i < noOfClients; i++){
                    		if(!messageInfo.senderUser.equals( client[i].clientUserName))
                    		{
                    			getClient(client[i].clientUserName).sendMessage(new MessageInfo("upload_req", messageInfo.senderUser, messageInfo.contentData, client[i].clientUserName));
                    		}
                    		
                    	}
                    }
                    else{
                        getClient(messageInfo.recipientUser).sendMessage(new MessageInfo("upload_req", messageInfo.senderUser, messageInfo.contentData, messageInfo.recipientUser));
                    }
                }
                else if(messageInfo.messageType.equals("upload_res")){
                    if(!messageInfo.contentData.equals("no")){
                        String ip = getClient(messageInfo.senderUser).socketAccept.getInetAddress().getHostAddress();
                        getClient(messageInfo.recipientUser).sendMessage(new MessageInfo("upload_res", ip, messageInfo.contentData, messageInfo.recipientUser));
                    }
                    else{
                        getClient(messageInfo.recipientUser).sendMessage(new MessageInfo("upload_res", messageInfo.senderUser, messageInfo.contentData, messageInfo.recipientUser));
                    }
                }
        }
        
    }
    
    //this method is use to broadcast message to all users
   public void sendMessageToAll(String messageType, String senderUser, String contentData){
        MessageInfo messageInfo = new MessageInfo(messageType, senderUser, contentData, "All");
        for(int i = 0; i < noOfClients; i++){
            client[i].sendMessage(messageInfo);
        }
    }
   //this method is use to broadcast list of users with ip addresses also
   public void sendListOfClients(String recUser){
        for(int i = 0; i < noOfClients; i++){
            getClient(recUser).sendMessage(new MessageInfo("newuser", "server", client[i].clientUserName+client[i].ipaddress, recUser));
        }
    }
   // by using this method getting Client details using userName on server side
   public Client getClient(String userName){
        for(int i = 0; i < noOfClients; i++){
            if(client[i].clientUserName.equals(userName)){
                return client[i];
            }
        }
        return null;
    }
  
  //this is remove user or client from server list
    public synchronized void removeClient(int uid) {
          int position = getClientPostion(uid);
	        if (position >= 0){  
	            Client removalClient = client[position];
	            System.out.println("Remove Client: " + uid + " from: " + position);
		    if (position < noOfClients-1){
	                for (int i = position+1; i < noOfClients; i++){
	                    client[i-1] = client[i];
		        }
		    }
		    noOfClients--;
		    try{  
		      	removalClient.closeChat();
		    }
		    catch(IOException ioe){  
		      	System.out.println("Error:" + ioe.getMessage()); 
		    }
	            if (clientThread != null){  
	             clientThread.interrupt();
	             clientThread = null;
	            }
		}
       
    }
    
    //adding user with server for handling request and response of clients
    private void joinClient(Socket accept) {
        if (noOfClients < client.length){  
            System.out.println("Client accepted: " + accept);
	    client[noOfClients] = new Client(this, accept);
	    try{  
	      	client[noOfClients].joinChat();
	        client[noOfClients].start();  
	        noOfClients++; 
	    }
	    catch(IOException ioe){  
	      	System.out.println("Error:" + ioe.getMessage()); 
	    } 
	}
	else{
            System.out.println("Maximum No of Clients are " + client.length);
	}
 
    }
    
}
