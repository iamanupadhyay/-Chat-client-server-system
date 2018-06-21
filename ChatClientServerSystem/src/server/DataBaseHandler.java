package server;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

import pojo.UserInfo;

public class DataBaseHandler {
	
  public String databaseFilepath;
	    ObjectOutputStream objectOutputStream;  
	    ObjectInputStream objectInputStream;
	    ArrayList<UserInfo> userInfos;
	    Component component;
	   
	    public DataBaseHandler(String databaseFilepath, Component component) {
	        this.databaseFilepath = databaseFilepath;
	        this.component = component;
	        
	    }
	    
	    //checking is user exits?
	    public boolean isUserExists(String userName){
	         boolean res=false;
	         try {
	                objectInputStream=new ObjectInputStream(new FileInputStream(databaseFilepath));  
	                userInfos=(ArrayList<UserInfo>)objectInputStream.readObject();  
	                objectInputStream.close();  
	              for(int i=0;i<userInfos.size();i++)
	              {
	            	  if(userInfos.get(i).getUserName().equals(userName) )
	            	  {
	            		  
	            		 res = true;
	            		 break;
	            	  }
	                  //System.out.println(userInfos.get(i).getFirstName());
	              }
	                System.out.println("success");  
	            } catch (Exception ex) {
	                System.out.println("Error:"+ex.getMessage());
	               // JOptionPane.showMessageDialog(component, ex.getMessage());
	            }
	        return res;
	    }
	    
	    // login method
	    public boolean loginMethod(String userName, String password){ 
	    	
	        boolean res=false;
	          try {
	                objectInputStream=new ObjectInputStream(new FileInputStream(databaseFilepath));  
	                userInfos=(ArrayList<UserInfo>)objectInputStream.readObject();  
	                objectInputStream.close();  
	              for(int i=0;i<userInfos.size();i++)
	              {
	            	  if(userInfos.get(i).getUserName().equals(userName) && userInfos.get(i).getPassword().equals(password))
	            	  {
	            		  
	            		 res = true;
	            		 break;
	            	  }
	                  //System.out.println(userInfos.get(i).getFirstName());
	              }
	                System.out.println("success");  
	            } catch (Exception ex) {
	                System.out.println("Error:"+ex.getMessage());
	               // JOptionPane.showMessageDialog(component, ex.getMessage());
	            }
	        return res;
	    }
	    
	    //sign up user
	    public boolean signUpMethod(String firstName, String lastName, String userName, String password){
	    	 boolean res=false;
	    	 
	        if(!new File(databaseFilepath).exists())
	        {
	             try {
	                userInfos=new ArrayList<>();
	                userInfos.add(new UserInfo(firstName, lastName, userName, password));
	                objectOutputStream = new ObjectOutputStream(new FileOutputStream(databaseFilepath));
	                objectOutputStream.writeObject(userInfos);  
	                objectOutputStream.flush();  
	                objectOutputStream.close();
	                res = true;
	                System.out.println("success");  
	            } catch (Exception ex) {
	                System.out.println("Error1:"+ex.getMessage());
	               // JOptionPane.showMessageDialog(component, ex.getMessage());
	            }
	        
	        }
	        else
	        {
	        	 if(isUserExists(userName))
                {
                	
                }
	        	 else
	        	 {
	        		 try {
	 	                objectInputStream=new ObjectInputStream(new FileInputStream(databaseFilepath));  
	 	                userInfos=(ArrayList<UserInfo>)objectInputStream.readObject();  
	 	                objectInputStream.close();
	 	                userInfos.add(new UserInfo(firstName, lastName, userName, password));
	 	                objectOutputStream = new ObjectOutputStream(new FileOutputStream(databaseFilepath));
	 	                objectOutputStream.writeObject(userInfos);  
	 	                objectOutputStream.flush();  
	 	                objectOutputStream.close();
	 	               res = true;
	 	                System.out.println("success"); 
	 	               
	 	            } catch (Exception ex) {
	 	                System.out.println("Error2:"+ex.getMessage());
	 	               // JOptionPane.showMessageDialog(component, ex.getMessage());
	 	            }
	        	 }
	           
	        }
	        return res;
	    }

}
