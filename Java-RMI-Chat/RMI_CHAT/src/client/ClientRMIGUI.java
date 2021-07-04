package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;

/**
 * 
 * @author Daragh Walshe 	B00064428
 * RMI Assignment 2		 	April 2015
 *
 */
public class ClientRMIGUI extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = 1L;	
	private JPanel textPanel, inputPanel;
	private JTextField textField;
	private String name, message;
	private Font meiryoFont = new Font("Meiryo", Font.PLAIN, 14);
	private Border blankBorder = BorderFactory.createEmptyBorder(10,10,20,10);//top,r,b,l
	private ChatClient3 chatClient;
    private JList<String> list;
    private DefaultListModel<String> listModel;
    
    protected JTextArea textArea, userArea;
    protected JFrame frame;
    protected JButton privateMsgButton, sendButton;
    protected JPanel clientPanel, userPanel;

	/**
	 * Main method to start client GUI app.
	 * @param args
	 */
	public static void main(String args[]){
		//set the look and feel to 'Nimbus'
		try{
			for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
				if("Nimbus".equals(info.getName())){
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		}
		catch(Exception e){
			}
		new ClientRMIGUI();
		}//end main
	
	
	/**
	 * GUI Constructor
	 */
	public ClientRMIGUI(){
			
		frame = new JFrame("Chat Application");	
	
		//-----------------------------------------
		/*
		 * intercept close method, inform server we are leaving
		 * then let the system exit.
		 */
		
		
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        
		    	if(chatClient != null){
			    	try {
			        	sendMessage("Au Revoir !!");
			        	chatClient.serverIF.leaveChat(name);
					} catch (RemoteException e) {
						e.printStackTrace();
					}		        	
		        }
		        System.exit(0);  
		    }   
		});
		//-----------------------------------------
		//remove window buttons and border frame
		//to force user to exit on a button
		//- one way to control the exit behaviour
	    //frame.setUndecorated(true);
	    //frame.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
		String result = JOptionPane.showInputDialog(frame, "Entrer votre Nom : ");
		if(result != "" && result != null ) {
			Container c = getContentPane();
			JPanel outerPanel = new JPanel(new BorderLayout());
			
			outerPanel.add(getInputPanel(), BorderLayout.CENTER);
			outerPanel.add(getTextPanel(), BorderLayout.NORTH);
			
			c.setLayout(new BorderLayout());
			c.add(outerPanel, BorderLayout.CENTER);
			c.add(getUsersPanel(), BorderLayout.WEST);

			frame.add(c);
			frame.pack();
			frame.setAlwaysOnTop(true);
			frame.setLocation(150, 150);
			textField.requestFocus();
			
			//////////////////////////////////////////////////////////////
			name = result;				
			if(name.length() != 0){
				frame.setTitle(name + " connecting");
				textField.setText("");
				textArea.append(name + " connect right now...\n");							
				try {
					getConnected(name);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(!chatClient.connectionProblem){
					sendButton.setEnabled(true);
					}
			}
			/////////////////////////////////////////////////////////////
		
			frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
			frame.setVisible(true);
		}else if(result == ""){
			JOptionPane.showMessageDialog(frame, "La prochaine fois enter votre Nom pour accéder");
		}else {
		
			JOptionPane.showMessageDialog(frame, "au revoir");
		}
		
	}
	
	
	/**
	 * Method to set up the JPanel to display the chat text
	 * @return
	 */
	public JPanel getTextPanel(){
		textArea = new JTextArea(14, 34);
		textArea.setMargin(new Insets(10, 10, 10, 10));
		textArea.setFont(meiryoFont);
		
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		textPanel = new JPanel();
		textPanel.add(scrollPane);
	
		textPanel.setFont(new Font("Meiryo", Font.PLAIN, 14));
		return textPanel;
	}
	
	/**
	 * Method to build the panel with input field
	 * @return inputPanel
	 */
	public JPanel getInputPanel(){
		inputPanel = new JPanel(new GridLayout(1, 1, 5, 5));
		inputPanel.setBorder(blankBorder);	
		textField = new JTextField();
		textField.setFont(meiryoFont);
		inputPanel.add(textField);
		return inputPanel;
	}

	/**
	 * Method to build the panel displaying currently connected users
	 * with a call to the button panel building method
	 * @return
	 */
	public JPanel getUsersPanel(){
		
		userPanel = new JPanel(new BorderLayout());
		String  userStr = " ---- Salon de Chat ----";
		
		JLabel userLabel = new JLabel(userStr, JLabel.CENTER);
		userPanel.add(userLabel, BorderLayout.NORTH);	
		userLabel.setFont(new Font("Meiryo", Font.PLAIN, 16));

		String[] noClientsYet = {"Le Salon est vide"};
		setClientPanel(noClientsYet);

		clientPanel.setFont(meiryoFont);
		userPanel.add(makeButtonPanel(), BorderLayout.SOUTH);		
		userPanel.setBorder(blankBorder);

		return userPanel;		
	}

	/**
	 * Populate current user panel with a 
	 * selectable list of currently connected users
	 * @param currClients
	 */
    public void setClientPanel(String[] currClients) {  	
    	clientPanel = new JPanel(new BorderLayout());
        listModel = new DefaultListModel<String>();
        
        for(String s : currClients){
        	listModel.addElement(s);
        }
        if(currClients.length > 1){
        	privateMsgButton.setEnabled(true);
        }
        
        //Create the list and put it in a scroll pane.
        list = new JList<String>(listModel);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setVisibleRowCount(8);
        list.setFont(meiryoFont);
        JScrollPane listScrollPane = new JScrollPane(list);

        clientPanel.add(listScrollPane, BorderLayout.CENTER);
        userPanel.add(clientPanel, BorderLayout.CENTER);
    }
	
	/**
	 * Make the buttons and add the listener
	 * @return
	 */
	public JPanel makeButtonPanel() {		
		sendButton = new JButton("Send ");
		sendButton.addActionListener(this);
		sendButton.setEnabled(false);

        privateMsgButton = new JButton("Envoyer");
        privateMsgButton.addActionListener(this);
        privateMsgButton.setEnabled(false);
		
		
		
		JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
		buttonPanel.add(new JLabel(""));
		buttonPanel.add(privateMsgButton);
		
		return buttonPanel;
	}
	
	/**
	 * Action handling on the buttons
	 */
	@Override
	public void actionPerformed(ActionEvent e){

		try {
			//send a private message, to selected users
			if(e.getSource() == privateMsgButton){
				int[] privateList = list.getSelectedIndices();
				
				for(int i=0; i<privateList.length; i++){
					System.out.println("selected index :" + privateList[i]);
				}
				message = textField.getText();
				textField.setText("");
				sendPrivate(privateList);
			}
			
		}
		catch (RemoteException remoteExc) {			
			remoteExc.printStackTrace();	
		}
		
	}//end actionPerformed

	// --------------------------------------------------------------------
	
	/**
	 * Send a message, to be relayed to all chatters
	 * @param chatMessage
	 * @throws RemoteException
	 */
	private void sendMessage(String chatMessage) throws RemoteException {
		chatClient.serverIF.updateChat(name, chatMessage);
	}

	/**
	 * Send a message, to be relayed, only to selected chatters
	 * @param chatMessage
	 * @throws RemoteException
	 */
	private void sendPrivate(int[] privateList) throws RemoteException {
		String privateMessage = "[" + name + "] :" + message + "\n";
		chatClient.serverIF.sendPM(privateList, privateMessage, name);
	}
	
	/**
	 * Make the connection to the chat server
	 * @param userName
	 * @throws RemoteException
	 */
	private void getConnected(String userName) throws RemoteException{
		//remove whitespace and non word characters to avoid malformed url
		String cleanedUserName = userName.replaceAll("\\s+","_");
		cleanedUserName = userName.replaceAll("\\W+","_");
		try {		
			chatClient = new ChatClient3(this, cleanedUserName);
			chatClient.startClient();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}//end class










