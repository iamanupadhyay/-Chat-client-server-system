package client;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

// in this thread we use to downloading file
public class DownloadFile implements Runnable{
    
     public ServerSocket serverSocket;
    public Socket socketdw;
      public int portNumber;
     public String saveToUser = "";
    public InputStream inputStream;
    public FileOutputStream fileOutputStream;
    public ChatBox chatBox;
    
  public DownloadFile(String saveToUser, ChatBox chatBox){
        try {
            serverSocket = new ServerSocket(0);
             portNumber = serverSocket.getLocalPort();
            this.saveToUser = saveToUser;
            this.chatBox = chatBox;
        } 
        catch (IOException ex) {
            System.out.println("Error:"+ex.getMessage());
        }
    }
    @Override
    public void run() {
          try {
            socketdw = serverSocket.accept();
            System.out.println("Download : "+socketdw.getRemoteSocketAddress());
            
            inputStream = socketdw.getInputStream();
            fileOutputStream = new FileOutputStream(saveToUser);
            
            byte[] buffer = new byte[1024];
            int count;
            
            while((count = inputStream.read(buffer)) >= 0){
                fileOutputStream.write(buffer, 0, count);
            }
            
            fileOutputStream.flush();
            
            chatBox.chatboxTextArea.append("[Application > Me] : Download complete\n");
            
            if(fileOutputStream != null){ fileOutputStream.close(); }
            if(inputStream != null){ inputStream.close(); }
            if(socketdw != null){ socketdw.close(); }
        } 
        catch (Exception ex) {
            System.out.println("Error:"+ex.getMessage());
        }

    }
    
}
