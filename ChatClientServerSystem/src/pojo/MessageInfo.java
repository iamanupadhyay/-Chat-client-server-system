package pojo;

import java.io.Serializable;

//this class or object use to sending message information between server and clients 
public class MessageInfo implements Serializable{
    public String messageType, senderUser, contentData, recipientUser;

    public MessageInfo(String messageType, String senderUser, String contentData, String recipientUser) {
        this.messageType = messageType;
        this.senderUser = senderUser;
        this.contentData = contentData;
        this.recipientUser = recipientUser;
      
    }
     @Override
    public String toString(){
        return "{messageType='"+messageType+"', senderUser='"+senderUser+"', contentData='"+contentData+"', recipientUser='"+recipientUser+"'}";
    }
}
