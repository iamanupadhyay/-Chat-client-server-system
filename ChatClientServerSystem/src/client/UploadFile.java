package client;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

//this thread use to uploading file
public class UploadFile implements Runnable{
    
    public Socket socketup;
    public FileInputStream fileInputStream;
    public OutputStream outputStream;
    public ChatBox chatBox;
     public UploadFile(String address, int portNumber, File fileup, ChatBox chatBox){
        super();
        try {
            this.chatBox = chatBox;
            socketup = new Socket(InetAddress.getByName(address), portNumber);
            outputStream = socketup.getOutputStream();
            fileInputStream = new FileInputStream(fileup);
        } 
        catch (Exception ex) {
            System.out.println("Error:"+ex.getMessage());
        }
    }
    
    @Override
    public void run() {
        try {       
                byte[] buffer = new byte[1024];
                int count;

                while((count = fileInputStream.read(buffer)) >= 0){
                    outputStream.write(buffer, 0, count);
                }
                outputStream.flush();

                chatBox.chatboxTextArea.append("[Applcation > Me] : File upload complete\n");
              
                if(fileInputStream != null){ 
                    fileInputStream.close(); 
                }
                if(outputStream != null){ 
                    outputStream.close(); 
                }
                if(socketup != null){ 
                    socketup.close(); 
                }
            }
            catch (Exception ex) {
                System.out.println("Error:"+ex.getMessage());
            }
    }
    
}
