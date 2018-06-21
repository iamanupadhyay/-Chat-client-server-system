package client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import pojo.MessageInfo;
import pojo.UserInfo;
//done
// this class is GUI for users or clients like chat box, chatting area
public class ChatBox extends JFrame implements ActionListener{
	
	  /*these are panels object contain component like mainPanel, main panel all panel are add in this panel
     * connectPanel contain all components are add which are use to connect client to server
     * signUpPanel contain all sign up component
     * loginPanel contain all login component
     */
    JPanel mainPanel,connectPanel,signUpPanel,loginPanel;
    
    /*these are separators for showing separate area of component
     */
    JSeparator jSeparator,jSeparator1,jSeparator2,jSeparator3;
    
    /*
     * these are textfield for getting user or client entered value
     * as name provide
     */
    JTextField ipAddressTextField,firstNameTextField,lastNameTextField
            ,userNameTextField,userNameTextField1,messageTextField;
    JPasswordField passwordField,passwordField1;
    
    /*
     * these are button object which is use to performed action
     * as name provide
     */
    JButton connectButton,signUpButton,loginButton,sendMessageButton
    	,browseButton,fileSendButton,blockUserButton;
    
    /*
     * this object is use to show all chat details
     */
    JTextArea chatboxTextArea;
    
    /*
     * one scroll pane for scrolling chat area
     * another for listing users or clients
     */
    JScrollPane jScrollPane,jScrollPane1;
    
    /*
     * this object use showing user or client list
     */
    JList userList;
    
    /*
     * these object are labeling for message, file, and file path
     */
    JLabel jLabel,jLabel1,filepathLabel;
    
    /*
     * these are variables use to store the user or client details
     * as name provide
     */
    public String serverAddress,userName,password,firstName,lastName;
    public int portNumber;
    
    /*
     * this object use to communicate with sever
     */
    public ClientSocket clientSocket;
    
    /*
     * this object act as user or client thread
     */
    public Thread userThread;
    
    /*
     * this is use listing or adding user or client list
     */
    public DefaultListModel defaultListModel;
    
    /*
     * this is use getting file path for download and upload file
     */
    public File file;
     
    /*
     * this is constructor use to initaite all object and showing chat box frame
     */
    public ChatBox() {
        
    	//here initializing main panel with null layout 
        mainPanel=new JPanel(null);
        
        //here initializing connect panel with default layout and also initializing all component which are use for connection
        ipAddressTextField=new JTextField(20);
        ipAddressTextField.setText("localhost");
        connectButton=new JButton("Connect");
        connectPanel=new JPanel();
        connectPanel.add(new JLabel("IP Address:"));
        connectPanel.add(ipAddressTextField);
        connectPanel.add(connectButton);
        connectPanel.setBounds(10, 10, 600, 45);
        
        //here initialing separator after showing connect panel
        jSeparator=new JSeparator();
        jSeparator.setBounds(10, 55, 580, 10);
       
      //here initializing sign up panel with default Grid layout and also initializing all component which are use for sign up
        firstNameTextField=new JTextField(20);
        lastNameTextField=new JTextField(20);
        userNameTextField=new JTextField(20);
        passwordField=new JPasswordField(20);
        signUpButton=new JButton("Sign Up");
        signUpPanel=new JPanel(new GridLayout(2, 5,5,5));
        signUpPanel.add(new JLabel("First Name:"));
        signUpPanel.add(firstNameTextField);
        signUpPanel.add(new JLabel("Last Name:"));
        signUpPanel.add(lastNameTextField);
        signUpPanel.add(new JLabel("  "));
        signUpPanel.add(new JLabel("User Name:"));
        signUpPanel.add(userNameTextField);
        signUpPanel.add(new JLabel("Password:"));
        signUpPanel.add(passwordField);
        signUpPanel.add(signUpButton);
        signUpPanel.setBounds(10, 70, 540, 50);
        
      //here initialing separator after showing sign up panel      
        jSeparator1=new JSeparator();
        jSeparator1.setBounds(10, 130, 580, 10);
        
      //here initializing login panel with default Grid layout and also initializing all component which are use for login
        userNameTextField1=new JTextField(20);
        passwordField1=new JPasswordField(20);
        loginButton=new JButton("Login");
        loginPanel=new JPanel(new GridLayout(1, 5, 5, 5));
        loginPanel.add(new JLabel("User Name:"));
        loginPanel.add(userNameTextField1);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField1);
        loginPanel.add(loginButton);
        loginPanel.setBounds(10, 145, 540, 23);
        
      //here initialing separator after showing login panel    
        jSeparator2=new JSeparator();
        jSeparator2.setBounds(10, 180, 580, 10);
       
     // here initializing chat box area with scroll
        chatboxTextArea=new JTextArea(); 
        jScrollPane=new JScrollPane();
        jScrollPane.setViewportView(chatboxTextArea);
        jScrollPane.setBounds(10, 195, 425, 225);
        
      //here initializing user list with scroll
        userList=new JList();
        jScrollPane1=new JScrollPane();
        userList.setModel((defaultListModel = new DefaultListModel()));
        jScrollPane1.setViewportView(userList);
        defaultListModel.addElement("All");
        userList.setSelectedIndex(0);
        jScrollPane1.setBounds(450, 195, 125, 190);
       
        //block button
        blockUserButton=new JButton("Block/UnBlock");
        blockUserButton.setBounds(450, 390, 125, 25);
        
      //here initialing all component which responsible send message
        jLabel=new JLabel("Message:");
        jLabel.setBounds(10, 435, 60 , 10);
        messageTextField=new JTextField();
        messageTextField.setBounds(80, 430, 355 , 25);
        sendMessageButton=new JButton("Send");
        sendMessageButton.setBounds(450, 430, 125, 25);
        
      //here initialing separator after showing send message component    
        jSeparator3=new JSeparator();
        jSeparator3.setBounds(10, 465, 580, 10);
        
      //here initialing all component which responsible send file
        jLabel1=new JLabel("File:");
        jLabel1.setBounds(10, 475, 60 , 25);
        filepathLabel=new JLabel("File Path...");
        filepathLabel.setBounds(80, 475, 300 , 25);
        browseButton=new JButton("Browse..");
        browseButton.setBounds(350, 475, 85 , 25);
        fileSendButton=new JButton("File Send");
        fileSendButton.setBounds(450, 475, 125 , 25);
     
        //here adding all component on main panel
        mainPanel.add(connectPanel);
        mainPanel.add(jSeparator);
        mainPanel.add(signUpPanel);
        mainPanel.add(jSeparator1);
        mainPanel.add(loginPanel);
        mainPanel.add(jSeparator2);
        mainPanel.add(jScrollPane);
        mainPanel.add(jScrollPane1);
        mainPanel.add(jLabel);
        mainPanel.add(messageTextField);
        mainPanel.add(blockUserButton);
        mainPanel.add(sendMessageButton);
        mainPanel.add(jSeparator3);
        mainPanel.add(jLabel1);
        mainPanel.add(filepathLabel);
        mainPanel.add(browseButton);
        mainPanel.add(fileSendButton);
        
        //here adding action listener on button component
        connectButton.addActionListener(this);
        signUpButton.addActionListener(this);
        loginButton.addActionListener(this);
        sendMessageButton.addActionListener(this);
        browseButton.addActionListener(this);
        fileSendButton.addActionListener(this);
        blockUserButton.addActionListener(this);
        
        //here set all configuration of JFrame
        disableAllComponent();
       setVisible(true);
       setSize(600, 550);
       setDefaultCloseOperation(EXIT_ON_CLOSE);
       setTitle("Chat Box");
       setResizable(false);
       add(mainPanel);
       
       
      
    }
    
    //disable all UI component
    void disableAllComponent()
    {
    	firstNameTextField.setEditable(false);
    	lastNameTextField.setEditable(false);
    	userNameTextField.setEditable(false);
    	passwordField.setEditable(false);
    	userNameTextField1.setEditable(false);
    	passwordField1.setEditable(false);
    	signUpButton.setEnabled(false);
    	loginButton.setEnabled(false);
    	blockUserButton.setEnabled(false);
    	sendMessageButton.setEnabled(false);
    	browseButton.setEnabled(false);
    	fileSendButton.setEnabled(false);
    	chatboxTextArea.setEditable(false);
    	userList.setEnabled(false);
    	messageTextField.setEditable(false);
    	
    	
    	
    }
    
    //enable all sign up and login component
    // and disable connect component
    public void enableSignUpAndLoginComponent() {
    	
    	firstNameTextField.setEditable(true);
    	lastNameTextField.setEditable(true);
    	userNameTextField.setEditable(true);
    	passwordField.setEditable(true);
    	userNameTextField1.setEditable(true);
    	passwordField1.setEditable(true);
    	signUpButton.setEnabled(true);
    	loginButton.setEnabled(true);
    	disableConnectComponent();
    	
	}
    
    //disable connect component
    public void disableConnectComponent() {
		ipAddressTextField.setEditable(false);
		connectButton.setEnabled(false);
	}
    
    //disable signup and login component
    public void disableSignUpAndLoginComponent() {
    	
    	firstNameTextField.setEditable(false);
    	lastNameTextField.setEditable(false);
    	userNameTextField.setEditable(false);
    	passwordField.setEditable(false);
    	userNameTextField1.setEditable(false);
    	passwordField1.setEditable(false);
    	signUpButton.setEnabled(false);
    	loginButton.setEnabled(false);
    	enableChatboxComponentForChatting();
	}
    
    //enable chat box component for chatting
    public void enableChatboxComponentForChatting() {
    	blockUserButton.setEnabled(true);
    	sendMessageButton.setEnabled(true);
    	browseButton.setEnabled(true);
    	fileSendButton.setEnabled(true);
    	//chatboxTextArea.setEditable(false);
    	userList.setEnabled(true);
    	messageTextField.setEditable(true);
		
	}
    
    //this is main method execution start from this method
    public static void main(String args[])
    {
    	//here calling constructor of chat box
        new ChatBox();
    }

    //this is method for performing task on all button click
    @Override
    public void actionPerformed(ActionEvent e) {
    	
    	//connect button task:- in this task we are getting ip address and port number for connecting with server
        if(e.getSource()==connectButton)
        {
              serverAddress = ipAddressTextField.getText();
              portNumber=5000;
        
            if(!serverAddress.isEmpty()){
                try{
                    clientSocket = new ClientSocket(this);
                    userThread = new Thread(clientSocket);
                    userThread.start();
                    clientSocket.sendMessage(new MessageInfo("connect", "connectUser", "connectContent", "server"));
                }
                catch(Exception ex){
                    chatboxTextArea.append("[Application > Me] : Server not found\n");
                }
            }
        }
        
        // sign up button:- sign up user or client
        if(e.getSource() == signUpButton)
        {
              password="";
              userName = userNameTextField.getText();
              char ps[]=passwordField.getPassword();
              for (int i = 0; i < ps.length; i++) {
                password +=String.valueOf(ps[i]);
               }
              firstName = firstNameTextField.getText();
              lastName = lastNameTextField.getText();
               

            if(!userName.isEmpty() && !password.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty()){
                clientSocket.sendMessage(new MessageInfo("signup", userName, password, "server"));
            }
            else
            {
            	JOptionPane.showMessageDialog(this, "fill all require field");
            	
            }
        }
        
        //login button:- login user or client
        if(e.getSource()==loginButton)
        {
            password="";
             userName = userNameTextField1.getText();
            char ps[]=passwordField1.getPassword();
              for (int i = 0; i < ps.length; i++) {
                password +=String.valueOf(ps[i]);
               }

            if(!userName.isEmpty() && !password.isEmpty()){
                clientSocket.sendMessage(new MessageInfo("login", userName, password, "server"));
            }
            else
            {
            	JOptionPane.showMessageDialog(this, "fill all require field");
            	
            }
        }
        
        // send message button:- sending message
        if(e.getSource()==sendMessageButton)
        {
             String message = messageTextField.getText();
            String targetUser = userList.getSelectedValue().toString().split("/")[0];

            if(!message.isEmpty() && !targetUser.isEmpty()){
                messageTextField.setText("");
                if(targetUser.equals("All"))
                {
                	for (int i = 0; i < defaultListModel.size(); i++) {
						System.out.println(defaultListModel.getElementAt(i));
						if(!defaultListModel.getElementAt(i).toString().contains("(x)") && !defaultListModel.getElementAt(i).toString().equals("") && !defaultListModel.getElementAt(i).toString().equals("All"))
						{
							clientSocket.sendMessage(new MessageInfo("newmessage", userName, message, defaultListModel.getElementAt(i).toString().split("/")[0]));
						}
						
					}
                }
                else
                {
                	clientSocket.sendMessage(new MessageInfo("newmessage", userName, message, targetUser));
                }
                
            }
        }
        
        //browse button :- choosing file for uploading to users or clients
        if(e.getSource()==browseButton)
        {
             JFileChooser fileChooser = new JFileChooser();
            fileChooser.showDialog(this, "Select File");
            file = fileChooser.getSelectedFile();

            if(file != null){
                if(!file.getName().isEmpty()){
                    //jButton6.setEnabled(true); 
                    String str;

                    if(filepathLabel.getText().length() > 30){
                        String t = file.getPath();
                        str = t.substring(0, 20) + " [...] " + t.substring(t.length() - 20, t.length());
                    }
                    else{
                        str = file.getPath();
                    }
                    filepathLabel.setText(str);
                }
            }
        }
        
        //file send button:- by using this button uploading selected file to users or clients
        if(e.getSource()==fileSendButton)
        {
             long fileSize = file.length();
            if(fileSize < 120 * 1024 * 1024){
                clientSocket.sendMessage(new MessageInfo("upload_req", userName, file.getName(), userList.getSelectedValue().toString().split("/")[0]));
            }
            else{
                chatboxTextArea.append("[Application > Me] : File is size too large\n");
            }
        }
        
        //block button:- by using this button block user or client
        if(e.getSource()==blockUserButton)
        {
        	int postion=userList.getSelectedIndex();
        	
        	if(!userList.getSelectedValue().toString().contains("(x)") && !userList.getSelectedValue().toString().equals("All"))
        	{
        		defaultListModel.add(postion,"(x)"+userList.getSelectedValue().toString());
            	defaultListModel.remove(postion+1);
        	}
        	else if(userList.getSelectedValue().toString().contains("(x)"))
        	{
        		String usr=userList.getSelectedValue().toString();
        		defaultListModel.add(postion, usr.substring(3));
        		defaultListModel.remove(postion+1);
        	}
        	
        }
    }
}
