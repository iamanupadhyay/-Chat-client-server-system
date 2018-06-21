package client;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import pojo.MessageInfo;

// this class is use to sending or receiving message between server and client
// it provide function of all button click from GUI
public class ClientSocket implements Runnable{
	
    public int portNumber;
    public String serverAddress;
    public Socket socketcl;
    public ChatBox chatBox;
    public ObjectInputStream objectInputStream;
    public ObjectOutputStream objectOutputStream;
    
    //this is use to initializing all instance 
    public ClientSocket(ChatBox chatBox) throws IOException{
        
        this.chatBox = chatBox; this.serverAddress = chatBox.serverAddress; this.portNumber = chatBox.portNumber;
        socketcl = new Socket(InetAddress.getByName(serverAddress), portNumber);
            
        objectOutputStream = new ObjectOutputStream(socketcl.getOutputStream());
        objectOutputStream.flush();
        objectInputStream = new ObjectInputStream(socketcl.getInputStream());
        
       
    }
    
    //this method is use to sending message information from client to server
     public void sendMessage(MessageInfo messageInfo){
        try {
        	if(messageInfo.recipientUser.contains("(x)"))
        	{
        		JOptionPane.showMessageDialog(chatBox, "User Block");
        	}
        	else
        	{
        		   objectOutputStream.writeObject(messageInfo);
                   objectOutputStream.flush();
                   System.out.println("Outgoing : "+messageInfo.toString());
        	}
         
          
        } 
        catch (IOException ex) {
            System.out.println("Error:"+ex.getMessage());
        }
    }
     // this is override thread method using  all functionally of client do response according to message broadcast by server
    @Override
    public void run() {
        boolean runningStatus=true;
        while(runningStatus){
            try{
                 MessageInfo messageInfo = (MessageInfo) objectInputStream.readObject();
                 
                System.out.println("Incoming : "+messageInfo.toString());
                
               //reading new message broadcast by server 
                if(messageInfo.messageType.equals("newmessage")){
                	
                	if(!messageInfo.recipientUser.equals("")){
                		//Receive message to me
                        if(messageInfo.recipientUser.equals(chatBox.userName)){
                            chatBox.chatboxTextArea.append("["+messageInfo.senderUser +" > Me] : " + messageInfo.contentData + "\n");
                        }
                        //receive message to all
                        else{
                            chatBox.chatboxTextArea.append("["+ messageInfo.senderUser +" > "+ messageInfo.recipientUser +"] : " + messageInfo.contentData + "\n");
                        }
                	}
                	
                  
                }
                
                //reading login command broadcast by server
                   else if(messageInfo.messageType.equals("login")){
                	   
                	   //if login done
                    if(messageInfo.contentData.equals("yes")){
                         chatBox.chatboxTextArea.append("[Server > Me] : Login Successful\n");
                         chatBox.disableSignUpAndLoginComponent();
                     }
                    
                    //login failed
                    else{
                        chatBox.chatboxTextArea.append("[Server > Me] : Login Failed\n");
                    }
                }
                
                // reading connect command from server broadcast message
                else if(messageInfo.messageType.equals("connect")){
                	chatBox.enableSignUpAndLoginComponent();
                	chatBox.chatboxTextArea.append("[Application > Me] : Connect Successfully\n");
                  
                }
                
                //reading new user command from server for adding in list
                else if(messageInfo.messageType.equals("newuser")){
                	
                    if(!messageInfo.contentData.split("/")[0].equals(chatBox.userName) && !messageInfo.contentData.startsWith("/")){
                        boolean exists = false;
                        for(int i = 0; i < chatBox.defaultListModel.getSize(); i++){
                            if(chatBox.defaultListModel.getElementAt(i).equals(messageInfo.contentData)){
                                exists = true; 
                                break;
                            }
                        }
                        if(!exists){ 
                           chatBox.defaultListModel.addElement(messageInfo.contentData); 
                        }
                    }
                }
                //reading signup command 
                  else if(messageInfo.messageType.equals("signup")){
                	  
                	  //if signup done
                    if(messageInfo.contentData.equals("yes")){
                         chatBox.chatboxTextArea.append("[Server > Me] : Singup Successful\n");
                    }
                    // signup failed
                    else{
                        chatBox.chatboxTextArea.append("[Server > Me] : Signup Failed\n");
                    }
                }
                
                // reading logout command
                 else if(messageInfo.messageType.equals("logout")){
                    if(messageInfo.contentData.equals(chatBox.userName)){
                        chatBox.chatboxTextArea.append("["+ messageInfo.senderUser +" > Me] : Bye\n");
                       
                        
                        for(int i = 1; i < chatBox.defaultListModel.size(); i++){
                            chatBox.defaultListModel.removeElementAt(i);
                        }
                        
                        chatBox.userThread.interrupt();
                        chatBox.userThread=null;
                    }
                    else{
                        chatBox.defaultListModel.removeElement(messageInfo.contentData);
                        chatBox.chatboxTextArea.append("["+ messageInfo.senderUser +" > All] : "+ messageInfo.contentData +" has signed out\n");
                    }
                }
                //reading downloading request for downloading
                  else if(messageInfo.messageType.equals("upload_req")){
                    
                    if(JOptionPane.showConfirmDialog(chatBox, ("Accept '"+messageInfo.contentData+"' from "+messageInfo.senderUser+" ?")) == 0){
                        
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setSelectedFile(new File(messageInfo.contentData));
                        int returnOption = fileChooser.showSaveDialog(chatBox);
                       
                        String saveToUser = fileChooser.getSelectedFile().getPath();
                        if(saveToUser != null && returnOption == JFileChooser.APPROVE_OPTION){
                            DownloadFile downloadFile = new DownloadFile(saveToUser, chatBox);
                            Thread downloadThread = new Thread(downloadFile);
                            downloadThread.start();
                            //sendMessage(new MessageInfo("upload_res", (""+InetAddress.getLocalHost().getHostAddress()), (""+downloadFile.port), messageInfo.senderUser));
                            sendMessage(new MessageInfo("upload_res", chatBox.userName, (""+downloadFile.portNumber), messageInfo.senderUser));
                        }
                        else{
                            sendMessage(new MessageInfo("upload_res", chatBox.userName, "no", messageInfo.senderUser));
                        }
                    }
                    else{
                        sendMessage(new MessageInfo("upload_res", chatBox.userName, "no", messageInfo.senderUser));
                    }
                }
              //reading uploading response for upload
                 else if(messageInfo.messageType.equals("upload_res")){
                    if(!messageInfo.contentData.equals("no")){
                        int portNumber  = Integer.parseInt(messageInfo.contentData);
                        String address = messageInfo.senderUser;
                        
                        UploadFile uploadFile = new UploadFile(address, portNumber, chatBox.file, chatBox);
                        Thread thread = new Thread(uploadFile);
                        thread.start();
                    }
                    else{
                        chatBox.chatboxTextArea.append("[Server > Me] : "+messageInfo.senderUser+" rejected file request\n");
                    }
                }
                else{
                    chatBox.chatboxTextArea.append("[Server > Me] : Unknown message type\n");
                }
            }catch(Exception e){
                  runningStatus = false;
                chatBox.chatboxTextArea.append("[Application > Me] : Connection Failure\n");
               
                
                for(int i = 1; i < chatBox.defaultListModel.size(); i++){
                    chatBox.defaultListModel.removeElementAt(i);
                }
                
                chatBox.userThread.interrupt();
                chatBox.userThread=null;
                System.out.println("Error:"+e.getMessage());
               
            }
        }
 
    }
    
    
}
