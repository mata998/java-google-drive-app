package client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;

import server.FileConvertor;
import server.Server;
import server.userClasses.User;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import java.awt.Color;

public class Client extends JFrame {

	// Login content pane
	private JPanel logInContentPane;
	private JButton btnLogIn;
	private JTextField textUsername;
	private JTextField textPassword;
	private JLabel lblUsername;
	private JLabel lblPassword;
	private JLabel lblServerDown;
	private JLabel lblLogIn;
	private JButton btnGoToRegister;
	private JLabel lblNoAccount;
	
	// App content pane
	private JPanel appContentPane;
	private JLabel lblWelcome;
	private JLabel lblYourFiles;
	private JComboBox comboBoxYourFiles;
	private JTextArea textArea;
	private JLabel lblSelectedFile;
	private JLabel lblUploadANew;
	private JLabel lblEnterFilePath;
	private JTextField textFilePath;
	private JButton btnUpload;
	
	// Register content pane
	private JPanel registerContentPane;
	private JButton btnRegister;
	private JTextField textRegPassword;
	private JLabel label;
	private JTextField textRegUsername;
	private JLabel label_1;
	private JLabel lblRegister;
	
	// My things
	static final String ERROR_MSG = "***error";
	static final String SUCCESS_MSG = "***success";
	static final String ITS_DIRECTORY = "***itsdirectory";
	static final String LOGIN_REQUEST = "***login_request";
	static final String REGISTER_REQUEST = "***register_request";
	static final String FILE_REQUEST = "***file_request";
	static final String UPLOAD_REQUEST = "***upload_request";
	static final String LINKONCHANGE_REQUEST = "***linkchange_request";
	static final String GETUSER_BY_SHARELINK = "***getuserbysharelink";
	static final String LOGOUT_REQUEST = "***logout_request";
	static final String SHARE_TO_REQUEST = "***share_to";
	static final String GETFILES_USER_REQUEST = "***getfilesuser";
	static final String FILE_FROM_USER_REQUEST = " ***filefromuser";
	static final String CREATE_FOLDER_REQUEST = "***createfolder";
	static final String DELETE_FOLDER_REQUEST = "***deletefolder";
	static final String RENAME_FOLDER_REQUEST = "***renamefolder";
	static final String MOVE_FILE_REQUEST = "***movefile";
	static final String FILES_IN_FOLDER = "***filesinfolder";
	Socket connectionSocket = null;
	BufferedReader fromServerStream = null;
	PrintStream toServerStream = null;
	ObjectInputStream objectFromServerStream = null;
	String serverMsg;
	User currentUser = new User();
	String selectedFilePath;
	String selectedFileName;
	String currentPath = "";
	String sharedFromCurrentPath = "";
	boolean itsVisit = false; ///////
	private JLabel lblHaveAccount;
	private JButton btnGoToLogin;
	private JButton btnLogOut;
	private JTextField textLink;
	private JButton btnLink;
	private JLabel lblLinkForSharing;
	private JButton btnDownload;
	private JLabel lblWhoSharedWith;
	private JComboBox comboBoxSharedFrom;
	private JLabel lblShareWith;
	private JComboBox comboBoxSharedTo;
	private JComboBox comboBoxTheirFiles;
	private JLabel lblTheirFiles;
	private JLabel lblYouSharedTo;
	private JTextField textShareTo;
	private JTextField textShareLink;
	private JLabel lblUseShareLink;
	private JButton btnVisit;
	private JButton btnShareTo;
	private JButton btnChoseFile;
	private JScrollPane scrollPane;
	private JLabel lblPremium;
	private JButton btnCreate;
	private JButton btnDelete;
	private JButton btnRename;
	private JButton btnMove;
	/////
	
	

	

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client frame = new Client();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	// THIS IS MAIN REALLY
	public Client() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(370, 135, 587, 482);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
			connectToServer();
		} catch (Exception e) {
			System.out.println("SERVER DOWN");
		} 
		
		setContentPane(getLogInContentPane());
//		setContentPane(getAppContentPane());
//		setContentPane(getRegisterContentPane());
		
	}
	
	
	// LOG IN CONTENT PANE
	private JPanel getLogInContentPane() {
		if (logInContentPane == null) {
			logInContentPane = new JPanel();
			logInContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			logInContentPane.setLayout(null);
			
			
			logInContentPane.add(getBtnLogIn());
			logInContentPane.add(getTextUsername());
			logInContentPane.add(getTextPassword());
			logInContentPane.add(getLblUsername());
			logInContentPane.add(getLblPassword());
			logInContentPane.add(getLblLogIn());
			logInContentPane.add(getBtnGoToRegister());
			logInContentPane.add(getLblNoAccount());		
			logInContentPane.add(getTextShareLink());
			logInContentPane.add(getLblUseShareLink());
			logInContentPane.add(getBtnVisit());
				
				
			
		}
		return logInContentPane;
	}
	
	// CONNECTING TO SERVER
	private void connectToServer() throws Exception {
		System.out.println("CLIENT\n");
		

		connectionSocket = new Socket("localhost", 3000);
		System.out.println("Connected to server succesfully");
		
		
		fromServerStream = 
						new BufferedReader(
							new InputStreamReader(
								connectionSocket.getInputStream()));
		
		toServerStream = 
						new PrintStream(
							connectionSocket.getOutputStream());
		
		objectFromServerStream = new ObjectInputStream(connectionSocket.getInputStream());
			
	}
	
	// BTN LOGIN 
	private JButton getBtnLogIn() {
		if (btnLogIn == null) {
			btnLogIn = new JButton("Log IN");
			btnLogIn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					
					try {
						String userName = textUsername.getText();
						String password = textPassword.getText();
						
						if (userName.equals("") ||
							password.equals(""))
						{
							JOptionPane.showMessageDialog(
									Client.this,
									"Please eneter both fields", 
									"Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						// SEND LOGIN REQUEST
						toServerStream.println(LOGIN_REQUEST);
						
						// SENDING USS;PASS
						toServerStream.println(userName + ";" + password);
						
						// GETTING AUTHENTICATION
						serverMsg = fromServerStream.readLine();
						
						// LOG IN SUCCESSFUL ----------
						if (serverMsg.equals(SUCCESS_MSG)) {
							System.out.println("\nYAY");
							
							System.out.println("Waiting for obj");
							// Get User object
							currentUser = (User) objectFromServerStream.readObject();
							
							System.out.println("Got object");
							currentUser.showAll();
							
							// CHANGE CONTENT PANE
							Client.this.setContentPane(getAppContentPane());
							Client.this.validate();
						}
						else {
							
							JOptionPane.showMessageDialog(
									Client.this,
									"That account doesnt exist", 
									"Error",
									JOptionPane.ERROR_MESSAGE);
							
						}
						
						
					} catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(
								Client.this,
								"Server stopped working", 
								"Error",
								JOptionPane.ERROR_MESSAGE);
						
						dispose();
					}
					
				}
			});
			btnLogIn.setBounds(243, 211, 89, 23);
		}
		return btnLogIn;
	}
	
	// BTN GO TO REGISTER
	private JButton getBtnGoToRegister() {
		if (btnGoToRegister == null) {
			btnGoToRegister = new JButton("Register");
			btnGoToRegister.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					
					// CHANGE CONTENT PANE
					Client.this.setContentPane(getRegisterContentPane());
					Client.this.validate();
					
				}
			});
			btnGoToRegister.setBounds(471, 346, 89, 23);
		}
		return btnGoToRegister;
	}
	
	// BTN VISIT
	private JButton getBtnVisit() {
		if (btnVisit == null) {
			btnVisit = new JButton("Visit");
			btnVisit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						String shareLink = textShareLink.getText();
						
						toServerStream.println(GETUSER_BY_SHARELINK);
						
						// Ask for user
						System.out.println("\nVisit " + shareLink);
						toServerStream.println(shareLink);
						
						
						// if user is found
						serverMsg = fromServerStream.readLine();
						
						if (serverMsg.equals(SUCCESS_MSG)) {
							System.out.println("User found, w8ing for obj");
							
							// ?????????????????????????????????
							currentUser = (User) objectFromServerStream.readObject();
							itsVisit = true;
							
						
							System.out.println("Got obj");
							
							// CHANGE CONTENT PANE
							Client.this.setContentPane(getAppContentPane());
							Client.this.validate();
						}
						else {
							JOptionPane.showMessageDialog(
									Client.this,
									"That share link is not available", 
									"Error",
									JOptionPane.ERROR_MESSAGE);
						}
						
					}
					catch (Exception ex) {
						JOptionPane.showMessageDialog(
								Client.this,
								"Server stopped working", 
								"Error",
								JOptionPane.ERROR_MESSAGE);
						
						dispose();
					}
				}
			});
			btnVisit.setBounds(243, 335, 89, 23);
		}
		return btnVisit;
	}
	//////////////////////////
	
	
	// APP CONTENT PANE
	private JPanel getAppContentPane() {
		if (appContentPane == null) {
			appContentPane = new JPanel();			
			appContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			appContentPane.setLayout(null);
			appContentPane.add(getLblWelcome());
			appContentPane.add(getLblYourFiles());
			appContentPane.add(getScrollPane());
			appContentPane.add(getLblSelectedFile());
			appContentPane.add(getLblUploadANew());
			appContentPane.add(getLblEnterFilePath());
			appContentPane.add(getTextFilePath());
			appContentPane.add(getBtnUpload());
			appContentPane.add(getBtnLogOut());
			appContentPane.add(getTextLink());
			appContentPane.add(getBtnLink());
			appContentPane.add(getLblLinkForSharing());
			appContentPane.add(getBtnDownload());
			appContentPane.add(getLblWhoSharedWith());
			appContentPane.add(getLblShareWith());
			appContentPane.add(getLblTheirFiles());
			appContentPane.add(getLblYouSharedTo());
			appContentPane.add(getTextShareTo());
			appContentPane.add(getBtnShareTo());
			appContentPane.add(getBtnChoseFile());
			appContentPane.add(getComboBoxTheirFiles());
			
			
			// Combobox tests
//			appContentPane.add(getComboBoxYourFilesTEST());
//			appContentPane.add(getComboBoxSharedFromTEST());
//			appContentPane.add(getComboBoxSharedToTEST());
			
			
			if (currentUser.isPremium()) {
				appContentPane.add(getLblPremium());
				appContentPane.add(getBtnCreate());
				appContentPane.add(getBtnDelete());
				appContentPane.add(getBtnRename());
				appContentPane.add(getBtnMove());
			}
			
			// combobox yourfiles
			if (currentUser.getFiles().isEmpty() == false) {
				appContentPane.add(getComboBoxYourFiles(currentUser.getFilesStringArr()));	
			}
			else {
				appContentPane.add(getComboBoxYourFiles(new String[] {"No files"}));
			}
			
			// combobox sharedTO
			if (currentUser.getSharedTo().isEmpty() == false) {
				appContentPane.add(getComboBoxSharedTo(currentUser.getSharedToUsersStringArr()));
			}
			else {
				appContentPane.add(getComboBoxSharedTo(new String[] {"No users"}));
			}
			
			// combobox sharedFROM
			if (currentUser.getSharedFrom().isEmpty() == false) {
				appContentPane.add(getComboBoxSharedFrom(currentUser.getSharedFromUsersStringArr()));
			}
			else {
				appContentPane.add(getComboBoxSharedFrom(new String[] {"No users"}));
			}
			     
			
			
			// REMOVE things that visiters cant do
			if (itsVisit) {
				lblWelcome.setText("Welcome to " + currentUser.getUsername() + "'s drive");
				lblWhoSharedWith.setText("Who shared to them");
				appContentPane.remove(lblLinkForSharing);
				appContentPane.remove(btnLink);
				appContentPane.remove(textLink);
				appContentPane.remove(lblShareWith);
				appContentPane.remove(lblYouSharedTo);
				appContentPane.remove(textShareTo);
				appContentPane.remove(comboBoxSharedTo);
				appContentPane.remove(lblEnterFilePath);
				appContentPane.remove(lblUploadANew);
				appContentPane.remove(textFilePath);
				appContentPane.remove(btnUpload);
				appContentPane.remove(btnShareTo);
				appContentPane.remove(btnChoseFile);
			}
			
		}
		return appContentPane;
	}

	// COMBOBOX YOUR FILES
	private JComboBox getComboBoxYourFiles(String[] filesToShow) {
			if (comboBoxYourFiles == null) {
				comboBoxYourFiles = new JComboBox();
				comboBoxYourFiles.setModel(new DefaultComboBoxModel(filesToShow));
				comboBoxYourFiles.setToolTipText("");
				comboBoxYourFiles.setBounds(10, 70, 96, 20);
				
				
				comboBoxYourFiles.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						
						try {
							selectedFileName = (String) comboBoxYourFiles.getSelectedItem();
							selectedFilePath = currentPath + selectedFileName;
							
							System.out.println("Selected file path: " + selectedFilePath);
							if (currentUser.getFiles().isEmpty()) {
								return;
							}
							
							if (selectedFilePath.endsWith("/...")) {
								System.out.println("vracaj nazad");
								
								selectedFilePath = selectedFilePath.substring(0, selectedFilePath.lastIndexOf("/"));
								if (selectedFilePath.contains("/")) {
									selectedFilePath = selectedFilePath.substring(0, selectedFilePath.lastIndexOf("/"));
									currentPath = selectedFilePath.substring(0, selectedFilePath.lastIndexOf("/")+1);
									
									selectedFileName = selectedFilePath.substring(selectedFilePath.lastIndexOf("/")+1);
								}
								else { // GO BACK TO root dir
									selectedFilePath = null;
									currentPath = "";
									
									comboBoxYourFiles.setModel(new DefaultComboBoxModel(currentUser.getFilesStringArr()));
									
									lblSelectedFile.setText("Selected file: ");
									lblYourFiles.setText("Selected folder: root");
									textArea.setText("");
									selectedFileName = null;
									
									
									return;
								}
								
							
							}
							
							// Request a file from server
							toServerStream.println(FILE_REQUEST);
							toServerStream.println(selectedFilePath);
							
							System.out.println("REQUESTED: " + selectedFilePath);
							
							
							// Check if its a directory
							serverMsg = fromServerStream.readLine();
							if (serverMsg.equals(ITS_DIRECTORY)) {
								
								// Check if its empty or not
								serverMsg = fromServerStream.readLine();
								if (serverMsg.equals(SUCCESS_MSG)) {
									
									String filesInFolderSemic = fromServerStream.readLine();
									System.out.println(filesInFolderSemic);
									
									String[] filesInFolderArr = filesInFolderSemic.split(";");
									comboBoxYourFiles.setModel(new DefaultComboBoxModel(filesInFolderArr));
									comboBoxYourFiles.insertItemAt("...", 0);
									
									lblSelectedFile.setText("Selected file: ");
									lblYourFiles.setText("Selected folder: " + selectedFileName);
									textArea.setText("");
									
									currentPath = currentPath + selectedFileName + "/";
									selectedFileName = null;
								}
								else { // Dir is empty
									System.out.println("Dir empty");
									
									comboBoxYourFiles.setModel(new DefaultComboBoxModel(new String[] {"..."}));
									
									lblSelectedFile.setText("Selected file: ");
									lblYourFiles.setText("Selected folder: " + selectedFileName);
									textArea.setText("");
									
									currentPath = currentPath + selectedFileName + "/";
									selectedFileName = null;
								}
								
							} // If its not directory then its file
							else {
								// Get file from server
								String requestedFileText = "";
								String oneLine;
								
								while (true) {
									oneLine = fromServerStream.readLine();
									
									if (oneLine.equals(SUCCESS_MSG)) {
										break;
									}
									
									requestedFileText = requestedFileText + oneLine + "\n";
								}
								
								textArea.setText(requestedFileText);
								
								if (itsVisit) {
									lblSelectedFile.setText("Selected file: " + selectedFileName + "  From: " + currentUser.getUsername());
								}
								else {
									lblSelectedFile.setText("Selected file: " + selectedFileName);
								}
							}
							
							
							
						}
						catch (Exception e) {
							e.printStackTrace();
							System.out.println("SERVER IS DOWN");
							
							JOptionPane.showMessageDialog(
									Client.this,
									"Server stopped working", 
									"Error",
									JOptionPane.ERROR_MESSAGE);
							
							dispose();
						}
					}
				});
			
			
			}
			return comboBoxYourFiles;
		}
	
	// BTN UPLOAD
	private JButton getBtnUpload() {
		if (btnUpload == null) {
			btnUpload = new JButton("Upload!");
			btnUpload.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					try {
						String filePath = textFilePath.getText();
						
						if (filePath.equals("")) {
							JOptionPane.showMessageDialog(
									Client.this,
									"Please eneter file path", 
									"Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						String fileText;
						try {
							fileText = FileConvertor.readFile(filePath);
						}
						catch (Exception e) {
							JOptionPane.showMessageDialog(
									Client.this,
									"That file doesnt exist!", 
									"Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						if (currentUser.isPremium() == false && currentUser.getFiles().size() == 5) {
							JOptionPane.showMessageDialog(
									Client.this,
									"You have 5 files, you can't upload more", 
									"Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						int index1 = filePath.lastIndexOf("\\")+1;
						int index2 = filePath.lastIndexOf("/")+1;
						
						if (index2 >index1) {
							index1 = index2;
						}
						
						String fileName = filePath.substring(index1);
						
						// SEND UPLOAD REQUEST
						toServerStream.println(UPLOAD_REQUEST);
						System.out.println("i wanna UPLOAD!!!");
						System.out.println(fileName);
						
						// SEND FILE NAME
						toServerStream.println(fileName);
						
						// SEND FILE PATH
						toServerStream.println(currentPath);
						
						// SEND FILE TEXT
						toServerStream.println(fileText);
						toServerStream.println(SUCCESS_MSG);
						
						// if its all ok
						serverMsg = fromServerStream.readLine();
						
						if (serverMsg.equals(SUCCESS_MSG)) {
							JOptionPane.showMessageDialog(
									Client.this,
									"File uploaded successfully!", 
									"Success",
									JOptionPane.INFORMATION_MESSAGE);
							
							// UPDATE CURRENTUSER FILES
							if (currentUser.getFiles().isEmpty() == false) {
								
								if (currentPath.equals("")) {
									currentUser.getFiles().add(fileName);									
								}
								
								// UPDATE COMBO BOX
								comboBoxYourFiles.addItem(fileName);
								
								textFilePath.setText("");
							}
							else {
								
								if (currentPath=="") {
									currentUser.getFiles().add(fileName);
								}
								
								// UPDATE COMBO BOX
								comboBoxYourFiles.insertItemAt(fileName, 0);
								comboBoxYourFiles.removeItemAt(1);
								
								textFilePath.setText("");
							}
						}
						
						
						
						
						
						
					}
					catch (Exception e) {
						System.out.println("SERVER IS DOWN");
						
						JOptionPane.showMessageDialog(
								Client.this,
								"Server stopped working", 
								"Error",
								JOptionPane.ERROR_MESSAGE);
						
						dispose();
					}
				}
			});
			btnUpload.setBounds(10, 410, 89, 23);
		}
		return btnUpload;
	}
	
	// BTN LOGOUT
	private JButton getBtnLogOut() {
		if (btnLogOut == null) {
			btnLogOut = new JButton("Log out");
			btnLogOut.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						
						int choice = JOptionPane.showConfirmDialog(
								Client.this,
								"Want to log out?",
								"Log out", 
								JOptionPane.YES_NO_OPTION);
						
						if ( choice != JOptionPane.YES_OPTION) {
							return;
						}
						
						toServerStream.println(LOGOUT_REQUEST);
						
						
						
						// Restart vars
						currentUser = new User();
						itsVisit = false;
						selectedFileName = null;
						selectedFilePath = null;
						currentPath = "";
						sharedFromCurrentPath = "";
						
						appContentPane = null;
						
						lblWelcome = null;
						lblWhoSharedWith = null;
						lblYourFiles = null;
						lblSelectedFile = null;
						lblTheirFiles = null;
						comboBoxYourFiles = null;
						comboBoxSharedFrom = null;
						comboBoxTheirFiles = null;
						comboBoxSharedTo = null;
						scrollPane = null;
						textArea = null;
						textFilePath = null;
						textShareTo = null;
						textLink = null;
						btnLink = null;
						btnCreate = null;
						btnDelete = null;
						btnRename = null;
						btnMove = null;
						
						
						
						logInContentPane = null;
						
						textUsername = null;
						textPassword = null;
						textShareLink = null;
						
						
						registerContentPane = null;
						
						textRegUsername = null;
						textRegPassword = null;
						
						
						
						// CHANGE CONTENT PANE
						Client.this.setContentPane(getLogInContentPane());
						Client.this.validate();
					}
					catch (Exception ex) {
						
					}
					
				}
			});
			btnLogOut.setBounds(471, 410, 89, 23);
		}
		return btnLogOut;
	}
	
	// BTN LINK
	private JButton getBtnLink() {
		if (btnLink == null) {
			
			btnLink = new JButton("TEXT");
			
			if (currentUser.isLinkOn()) {
				btnLink.setText("Link ON");
			}
			else {
				btnLink.setText("Link OFF");
			}
			
			
			btnLink.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					try {
						toServerStream.println(LINKONCHANGE_REQUEST);
						
						// TURN OFF
						if (btnLink.getText().equals("Link ON")) {
							textLink.setText("");
							
							btnLink.setText("Link OFF");
							
							
							
						} // TURN ON
						else if (btnLink.getText().equals("Link OFF")) {
							textLink.setText(currentUser.getLink());
							
							btnLink.setText("Link ON");
							
						}
						
						serverMsg = fromServerStream.readLine();
						
						if (serverMsg.equals(SUCCESS_MSG) == false) {
							JOptionPane.showMessageDialog(
									Client.this,
									"Something went wrong!", 
									"Error",
									JOptionPane.ERROR_MESSAGE);
						}
						
					}
					catch (Exception e) {
						System.out.println("SERVER IS DOWN");
						
						JOptionPane.showMessageDialog(
								Client.this,
								"Server stopped working", 
								"Error",
								JOptionPane.ERROR_MESSAGE);
						
						dispose();
					}
					
				}
			});
			btnLink.setBounds(471, 28, 89, 23);
		}
		return btnLink;
	}
	
	// BTN SHARE TO
	private JButton getBtnShareTo() {
		if (btnShareTo == null) {
			btnShareTo = new JButton("Share");
			btnShareTo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					try {
						String targetUsername = textShareTo.getText();
						
						if (targetUsername.equals("")) {
							JOptionPane.showMessageDialog(
									Client.this,
									"Enter field!", 
									"Error",
									JOptionPane.ERROR_MESSAGE);
							
							return;
						}
						
						if (targetUsername.equals(currentUser.getUsername())) {
							JOptionPane.showMessageDialog(
									Client.this,
									"You can't share to yourself", 
									"Error",
									JOptionPane.ERROR_MESSAGE);
							
							return;
						}
						
						if (currentUser.sharedToUserExists(targetUsername)) {
							JOptionPane.showMessageDialog(
									Client.this,
									"You already shared to them!", 
									"Error",
									JOptionPane.ERROR_MESSAGE);
							
							return;
						}

						toServerStream.println(SHARE_TO_REQUEST);
						
						//check if that user exists
						toServerStream.println(targetUsername);
						
						// server answer
						serverMsg = fromServerStream.readLine();
						if (serverMsg.equals(ERROR_MSG)) {
							JOptionPane.showMessageDialog(
									Client.this,
									"That user doesn't exist", 
									"Error",
									JOptionPane.ERROR_MESSAGE);
							
							return;
						}
						
						
						// if its successfull
						serverMsg = fromServerStream.readLine();
						if (serverMsg.equals(SUCCESS_MSG)) {
						
							JOptionPane.showMessageDialog(
									Client.this,
									"Shared successfully", 
									"Success",
									JOptionPane.INFORMATION_MESSAGE);
							
							if (currentUser.getSharedTo().isEmpty()) {
								comboBoxSharedTo.insertItemAt(targetUsername,0);
								comboBoxSharedTo.removeItemAt(1);
								currentUser.addSharedToUser(targetUsername);
							}
							else {
								comboBoxSharedTo.addItem(targetUsername);
								currentUser.addSharedToUser(targetUsername);
							}
							
							
						}
						
						
					}
					catch (Exception e) {
//						e.printStackTrace();
						System.out.println("SERVER IS DOWN");
						
						JOptionPane.showMessageDialog(
								Client.this,
								"Server stopped working", 
								"Error",
								JOptionPane.ERROR_MESSAGE);
						
						dispose();
					}
				}
			});
			btnShareTo.setBounds(10, 283, 89, 23);
		}
		return btnShareTo;
	}
	
	// COMBOBOX SHARED TO
	private JComboBox getComboBoxSharedTo(String[] listOfItems) {
		if (comboBoxSharedTo == null) {
			comboBoxSharedTo = new JComboBox();
			comboBoxSharedTo.setModel(new DefaultComboBoxModel(listOfItems));
			comboBoxSharedTo.setToolTipText("");
			comboBoxSharedTo.setBounds(144, 252, 96, 20);
		
		
			comboBoxSharedTo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					
					
				}
			});
		
		}
		return comboBoxSharedTo;
	}
	
	// COMBOBOX SHARED FROM
	private JComboBox getComboBoxSharedFrom(String[] listOfItems) {
		if (comboBoxSharedFrom == null) {
			comboBoxSharedFrom = new JComboBox();
			comboBoxSharedFrom.setModel(new DefaultComboBoxModel(listOfItems));
			comboBoxSharedFrom.setToolTipText("");
			comboBoxSharedFrom.setBounds(10, 192, 121, 20);
		
			comboBoxSharedFrom.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					try {
						String selectedUser = (String) comboBoxSharedFrom.getSelectedItem();
						
						if (currentUser.getSharedFrom().isEmpty()) {
							return;
						}
						
						sharedFromCurrentPath = "";
						selectedFileName = null;
						textArea.setText("");
						lblTheirFiles.setText("Selected folder: root");
						lblSelectedFile.setText("Selected file: ");
						
						String[] listOfFiles = getFilesFromServer(selectedUser);
						
						if (listOfFiles[0].equals("") == false) {
							comboBoxTheirFiles.setEnabled(true);
							comboBoxTheirFiles.setModel(new DefaultComboBoxModel(listOfFiles));
						}
						else {
							comboBoxTheirFiles.setEnabled(false);
							comboBoxTheirFiles.setModel(new DefaultComboBoxModel(new String[] {"No files"}));
						}
						
					}
					catch (Exception e) {
						System.out.println("SERVER IS DOWN");
						
						JOptionPane.showMessageDialog(
								Client.this,
								"Server stopped working", 
								"Error",
								JOptionPane.ERROR_MESSAGE);
						
						dispose();
					}
				}
			});
		
			
			
		}
		return comboBoxSharedFrom;
	}
	
	private String[] getFilesFromServer(String targetUsername) {
		String filesFromServer = "";
		try {
			toServerStream.println(GETFILES_USER_REQUEST);
			
			// Ask for users files
			toServerStream.println(targetUsername);
			
			// Get files
			serverMsg = fromServerStream.readLine();
			
			if (serverMsg.equals(ERROR_MSG) == false) {
				
				filesFromServer = serverMsg;
				
			}
			
		}
		catch (Exception e) {
			
		}
		
		return filesFromServer.split(";");
	}
	
	// COMBOBOX THEIR FILES
	private JComboBox getComboBoxTheirFiles() {
		if (comboBoxTheirFiles == null) {
			comboBoxTheirFiles = new JComboBox();
			comboBoxTheirFiles.setEnabled(false);
			comboBoxTheirFiles.setModel(new DefaultComboBoxModel(new String[] {"Select user"}));
			comboBoxTheirFiles.setToolTipText("");
			comboBoxTheirFiles.setBounds(144, 192, 96, 20);
			
			comboBoxTheirFiles.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					
					try {
						selectedFileName = (String) comboBoxTheirFiles.getSelectedItem();
						
						selectedFilePath = sharedFromCurrentPath + selectedFileName;
						String selectedUser = (String) comboBoxSharedFrom.getSelectedItem();
						
						if (selectedFilePath.equals("Select user") || 
							selectedFilePath.equals("No files")) 
						{
							return;
						}
						
						
						if (selectedFilePath.endsWith("/...")) {
							System.out.println("vracaj nazad");
							
							
							selectedFilePath = selectedFilePath.substring(0, selectedFilePath.lastIndexOf("/"));
							if (selectedFilePath.contains("/")) { // Go back one folder
								
								selectedFilePath = selectedFilePath.substring(0, selectedFilePath.lastIndexOf("/"));
								sharedFromCurrentPath = selectedFilePath.substring(0, selectedFilePath.lastIndexOf("/")+1);
							
								selectedFileName = selectedFilePath.substring(selectedFilePath.lastIndexOf("/")+1);
							}
							else { // GO BACK TO root folder
								selectedFilePath = null;
								sharedFromCurrentPath = "";
								
								
								String[] listOfFiles = getFilesFromServer(selectedUser);
								
								if (listOfFiles[0].equals("") == false) {
									comboBoxTheirFiles.setEnabled(true);
									comboBoxTheirFiles.setModel(new DefaultComboBoxModel(listOfFiles));
								}
								else {
									comboBoxTheirFiles.setEnabled(false);
									comboBoxTheirFiles.setModel(new DefaultComboBoxModel(new String[] {"No files"}));
								}
								
								lblSelectedFile.setText("Selected file: ");
								lblTheirFiles.setText("Selected folder: root");
								textArea.setText("");
								selectedFileName = null;
								
								return;
							}
							
						
						}
						
						// Request a file from server
						toServerStream.println(FILE_FROM_USER_REQUEST);
						toServerStream.println(selectedFilePath);
						toServerStream.println(selectedUser);
						
						System.out.println("REQUESTED: " + selectedFilePath);
						
						
						// Check if its a directory
						serverMsg = fromServerStream.readLine();
						if (serverMsg.equals(ITS_DIRECTORY)) {
							System.out.println("its directoryyyyyyyyy");
							
							// check if its empty or not
							serverMsg = fromServerStream.readLine();
							if (serverMsg.equals(SUCCESS_MSG)) {
								
								String filesInFolderSemic = fromServerStream.readLine();
								System.out.println(filesInFolderSemic);
								
								String[] filesInFolderArr = filesInFolderSemic.split(";");
								comboBoxTheirFiles.setModel(new DefaultComboBoxModel(filesInFolderArr));
								comboBoxTheirFiles.insertItemAt("...", 0);
								
								lblSelectedFile.setText("Selected file: ");
								lblTheirFiles.setText("Selected folder: " + selectedFileName);
								textArea.setText("");
								
								sharedFromCurrentPath = sharedFromCurrentPath + selectedFileName + "/";
								selectedFileName = null;
							}
							else { // DIR IS EMPTY
								
								comboBoxTheirFiles.setModel(new DefaultComboBoxModel(new String[] {"..."}));
								
								lblSelectedFile.setText("Selected file: ");
								lblTheirFiles.setText("Selected folder: " + selectedFileName);
								textArea.setText("");
								
								sharedFromCurrentPath = sharedFromCurrentPath + selectedFileName + "/";
								selectedFileName = null;
								
							}
							
							
						}
						else {
							// Get file from server
							String requestedFileText = "";
							String oneLine;
							
							while (true) {
								oneLine = fromServerStream.readLine();
								
								if (oneLine.equals(SUCCESS_MSG)) {
									break;
								}
								
								requestedFileText = requestedFileText + oneLine + "\n";
							}
							
							textArea.setText(requestedFileText);
							lblSelectedFile.setText("You selected: " + selectedFileName + "  From: " + selectedUser);
							
						}
						
						
					}
					catch (Exception e) {
						System.out.println("SERVER IS DOWN");
						
						JOptionPane.showMessageDialog(
								Client.this,
								"Server stopped working", 
								"Error",
								JOptionPane.ERROR_MESSAGE);
						
						dispose();
					}
				}
			});
		
			
		}
		return comboBoxTheirFiles;
	}
	
	// BTN DOWNLOAD
	private JButton getBtnDownload() {
		if (btnDownload == null) {
			btnDownload = new JButton("Download");
			btnDownload.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (selectedFileName == null) {
						JOptionPane.showMessageDialog(
								Client.this,
								"Select a file!", 
								"Error",
								JOptionPane.ERROR_MESSAGE);
						
						return;
					}
					
					String selectedFileText = textArea.getText().trim();					
					String fileDirectory = "";
					
			
					JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory()); 
		            j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		            int choice = j.showDialog(Client.this, "Download");
		            
		            
		            if (choice == JFileChooser.APPROVE_OPTION) { 
		            	fileDirectory = j.getSelectedFile().getAbsolutePath() + "/";
		            }
		            else {
		            	return;
		            }
		            
					FileConvertor.textToFile(selectedFileText, fileDirectory+selectedFileName);
					System.out.println();
					
					JOptionPane.showMessageDialog(
							Client.this,
							"File dowloaded!", 
							"Success",
							JOptionPane.INFORMATION_MESSAGE);
				}
			});
			btnDownload.setBounds(295, 410, 100, 23);
		}
		return btnDownload;
	}
	
	// BTN CHOOSE FILE PATH
	private JButton getBtnChoseFile() {
		if (btnChoseFile == null) {
			btnChoseFile = new JButton("Chose file");
			btnChoseFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					
					
					JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory()); 
		            j.setFileSelectionMode(JFileChooser.FILES_ONLY);
		            int choice = j.showDialog(Client.this, "Choose");
		            
		            
		            if (choice == JFileChooser.APPROVE_OPTION) { 
		            	textFilePath.setText(j.getSelectedFile().getAbsolutePath());
		            }
		            else {
		            	return;
		            }
					
				}
			});
			btnChoseFile.setBounds(151, 378, 100, 23);
		}
		return btnChoseFile;
	}
	
	// BTN CREATE
	private JButton getBtnCreate() {
		if (btnCreate == null) {
			btnCreate = new JButton("Create");
			btnCreate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					
					CreateFolder createFolderJDialog = new CreateFolder(Client.this);
					createFolderJDialog.setVisible(true);
					
				}
			});
			btnCreate.setBounds(10, 101, 89, 23);
		}
		return btnCreate;
	}
	
	public void createFolder(String newFolderName) {
		try {
			System.out.println("Current path:" + currentPath);
			System.out.println("Create this: " + newFolderName);
			
			toServerStream.println(CREATE_FOLDER_REQUEST);
			
			// Send new folder path
			toServerStream.println(currentPath + newFolderName);
			
			// Server checks if it exists or not
			serverMsg = fromServerStream.readLine();
			if (serverMsg.equals(SUCCESS_MSG)) {
				
				
				// check if it is made
				serverMsg = fromServerStream.readLine();
				if (serverMsg.equals(SUCCESS_MSG)) {
					
					JOptionPane.showMessageDialog(
							Client.this,
							"Folder made successfully!", 
							"Success",
							JOptionPane.INFORMATION_MESSAGE);
					
					
					
					// Show it in combobox
					comboBoxYourFiles.addItem(newFolderName);
					
					// If its root dir, update currentUser files
					if (currentPath.equals("")) {
						
						currentUser.addFile(newFolderName);
						
					}
				}
				else {
					
					JOptionPane.showMessageDialog(
							Client.this,
							"Server could not make that folder", 
							"Error",
							JOptionPane.ERROR_MESSAGE);
					
				}
				
				
				
			}
			else { // THAT EXISTS
				JOptionPane.showMessageDialog(
						Client.this,
						"That folder already exists", 
						"Error",
						JOptionPane.ERROR_MESSAGE);
			}
			
			
		} catch (Exception e) {
			System.out.println("SERVER IS DOWN");
			
			JOptionPane.showMessageDialog(
					Client.this,
					"Server stopped working", 
					"Error",
					JOptionPane.ERROR_MESSAGE);
			
			dispose();
		}
	}
	
	// BTN DELETE
	private JButton getBtnDelete() {
		if (btnDelete == null) {
			btnDelete = new JButton("Delete");
			btnDelete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						
						System.out.println("CURRENT PATH: " + currentPath);
						
						if (currentPath.equals("")) {
							JOptionPane.showMessageDialog(
									Client.this,
									"Can't delete root folder", 
									"Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						if (comboBoxYourFiles.getItemAt(0).equals("...") &&
							comboBoxYourFiles.getItemCount() == 1)
						{
							
							int choice = JOptionPane.showConfirmDialog(
									Client.this,
									"You sure?",
									"Delete", 
									JOptionPane.YES_NO_OPTION);
							
							if ( choice != JOptionPane.YES_OPTION) {
								return;
							}
							
							System.out.println("you can delete this");
							toServerStream.println(DELETE_FOLDER_REQUEST);
							
							// Send folder path to server
							toServerStream.println(currentPath);
							
							
							// check if its deleted successfully
							serverMsg = fromServerStream.readLine();
							
							
							// Successful delete
							if (serverMsg.equals(SUCCESS_MSG)) {
								JOptionPane.showMessageDialog(
										Client.this,
										"Successfull delete", 
										"Error",
										JOptionPane.INFORMATION_MESSAGE);
								
								
								// if you delete file thats in the root
								if (currentPath.indexOf("/") == currentPath.lastIndexOf("/")) {
									
									currentUser.removeFile(currentPath.substring(0,currentPath.length()-1));
									
									System.out.println("Remove: " + currentPath.substring(0,currentPath.length()));
									
									System.out.println("User after delete: ");
									currentUser.showAll();
									
								}
								
								// go back to root dir
								selectedFilePath = null;
								currentPath = "";
								
								comboBoxYourFiles.setModel(new DefaultComboBoxModel(currentUser.getFilesStringArr()));
								
								lblSelectedFile.setText("Selected file: ");
								lblYourFiles.setText("Selected folder: root");
								textArea.setText("");
								selectedFileName = null;
								
								
							}
							else {
								JOptionPane.showMessageDialog(
										Client.this,
										"Server could not delete it", 
										"Error",
										JOptionPane.ERROR_MESSAGE);
								return;
							}
							
						}
						else {
							JOptionPane.showMessageDialog(
									Client.this,
									"Folder is not empty!", 
									"Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
					
					}
					catch (Exception ex) {
						JOptionPane.showMessageDialog(
								Client.this,
								"Server stopped working", 
								"Error",
								JOptionPane.ERROR_MESSAGE);
						
						dispose();
					}
					
				}
			});
			btnDelete.setBounds(109, 101, 89, 23);
		}
		return btnDelete;
	}
	
	// BTN RENAME
	private JButton getBtnRename() {
		if (btnRename == null) {
			btnRename = new JButton("Rename");
			btnRename.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					if (currentPath.equals("")) {
						JOptionPane.showMessageDialog(
								Client.this,
								"Can't rename root folder", 
								"Error",
								JOptionPane.ERROR_MESSAGE);
					}
					else {
						RenameFolder renameFolderJDialog = new RenameFolder(Client.this);
						renameFolderJDialog.setVisible(true);
					}
					
				}
			});
			btnRename.setBounds(10, 135, 89, 23);
		}
		return btnRename;
	}

	public void renameFolder(String newFolderName) {
		try {
			System.out.println("Current path: " + currentPath);
			System.out.println("New folder name: " + newFolderName);
			
			String oldFolderName = currentPath.split("/")[currentPath.split("/").length-1];
			System.out.println("Old folder name: " + oldFolderName);
			
			
			toServerStream.println(RENAME_FOLDER_REQUEST);
			
			// send folder path
			toServerStream.println(currentPath.substring(0, currentPath.length()-1));
			
			// send new name
			toServerStream.println(newFolderName);
			
			
			//check if it succeeded
			serverMsg = fromServerStream.readLine();
			if (serverMsg.equals(SUCCESS_MSG)) {
				
				// if you rename file thats in the root
				if (currentPath.indexOf("/") == currentPath.lastIndexOf("/")) {
					
					currentUser.renameFile(oldFolderName, newFolderName);
					
					System.out.println("Rename: " + oldFolderName + " to " + newFolderName);
					
					System.out.println("User after rename: ");
					currentUser.showAll();
					
				}
				
				
				// go back to root dir
				selectedFilePath = null;
				currentPath = "";
				
				comboBoxYourFiles.setModel(new DefaultComboBoxModel(currentUser.getFilesStringArr()));
				
				lblSelectedFile.setText("Selected file: ");
				lblYourFiles.setText("Selected folder: root");
				textArea.setText("");
				selectedFileName = null;
				
				
				JOptionPane.showMessageDialog(
						Client.this,
						"Folder renamed successfully", 
						"Success",
						JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				JOptionPane.showMessageDialog(
						Client.this,
						"Server can't rename that folder", 
						"Error",
						JOptionPane.ERROR_MESSAGE);
				
			}
			
			
		}
		catch (Exception e) {
//			e.printStackTrace();
			
			JOptionPane.showMessageDialog(
					Client.this,
					"Server stopped working", 
					"Error",
					JOptionPane.ERROR_MESSAGE);
			
			dispose();
		}
	}
	
	// BTN MOVE
	private JButton getBtnMove() {
			if (btnMove == null) {
				btnMove = new JButton("Move");
				btnMove.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						if (selectedFileName == null) {
							JOptionPane.showMessageDialog(
									Client.this,
									"Select a file!", 
									"Error",
									JOptionPane.ERROR_MESSAGE);
							
							return;
						}
						
						System.out.println("File path: " + selectedFilePath);
						System.out.println("Current folder: " + currentPath);
						
						
						// get dirs in this folder
						
						String dirsHereSemic = dirsInFolder(currentPath);
						
						System.out.println("Dirsheresemic: " + dirsHereSemic);
						
						if (dirsHereSemic != null) {
							
//							MoveFile moveFileJDialog = new MoveFile(Client.this, dirsHereSemic.split(";"));
							MoveFile moveFileJDialog = new MoveFile(Client.this, dirsHereSemic);
							
							moveFileJDialog.setVisible(true);
							
						}
						else {
							
							MoveFile moveFileJDialog = new MoveFile(Client.this, null);            
							moveFileJDialog.setVisible(true);
							
						}
					
					}
				});
				btnMove.setBounds(109, 135, 89, 23);
			}
			return btnMove;
		}
	
	private String dirsInFolder(String folderPath) {
		String dirsSemic;
		
		try {
			toServerStream.println(FILES_IN_FOLDER);
			
			// send file path
			toServerStream.println(folderPath);
			
			// check if it exists
			serverMsg=fromServerStream.readLine();
			if (serverMsg.equals(SUCCESS_MSG)) {
				
				// check if there are dirs
				serverMsg=fromServerStream.readLine();
				if (serverMsg.equals(SUCCESS_MSG)) {
					
					// get dirs
					serverMsg=fromServerStream.readLine();
					
					dirsSemic = serverMsg;
					
					return dirsSemic;
					
				}
				else { // there are no dirs
					return null;
					
				}
				
				
			}
			else {
				System.out.println("Problem");
			}
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	////////////////////////////////////
	
	
	// REGISTER CONTENT PANE
	private JPanel getRegisterContentPane() {
		if (registerContentPane == null) {
			registerContentPane = new JPanel();
			registerContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			registerContentPane.setLayout(null);
			registerContentPane.add(getBtnRegister());
			registerContentPane.add(getTextRegPassword());
			registerContentPane.add(getLabel());
			registerContentPane.add(getTextRegUsername());
			registerContentPane.add(getLabel_1());
			registerContentPane.add(getLblRegister());
			registerContentPane.add(getLblHaveAccount());
			registerContentPane.add(getBtnGoToLogin());
			
			
		}
		return registerContentPane;
	}
	
	// BTN REGISTER 
	private JButton getBtnRegister() {
		if (btnRegister == null) {
			btnRegister = new JButton("Register!");
			btnRegister.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						
						String userName = textRegUsername.getText();
						String password = textRegPassword.getText();
						
						if (userName.equals("") ||
							password.equals(""))
						{
							JOptionPane.showMessageDialog(
									Client.this,
									"Please eneter both fields", 
									"Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						// SEND REGISTER REQUEST
						toServerStream.println(REGISTER_REQUEST);
						
						// SENDING USS;PASS
						toServerStream.println(userName + ";" + password);
						
						// GETTING username availability
						serverMsg = fromServerStream.readLine();
						
						
						if (serverMsg.equals(SUCCESS_MSG)) {
							System.out.println("SUCCESS");
							
							// Get User object from server
							currentUser = (User) objectFromServerStream.readObject();
							
							JOptionPane.showMessageDialog(
									Client.this,
									"Successfully registered!", 
									"Success",
									JOptionPane.INFORMATION_MESSAGE);
							
							// CHANGE CONTENT PANE
							Client.this.setContentPane(getAppContentPane());
							Client.this.validate();
							
						}
						else {
							System.out.println("USERNAME NOT AVAILABLE");
							
							JOptionPane.showMessageDialog(
									Client.this,
									"Username is not available", 
									"Error",
									JOptionPane.ERROR_MESSAGE);
						}
						
						
					}
					catch (Exception ex) {
						JOptionPane.showMessageDialog(
								Client.this,
								"Server stopped working", 
								"Error",
								JOptionPane.ERROR_MESSAGE);
						
						dispose();
					}
					
				}
			});
			btnRegister.setBounds(243, 211, 89, 23);
		}
		return btnRegister;
	}
	
	// BTN GO TO LOGIN
	private JButton getBtnGoToLogin() {
		if (btnGoToLogin == null) {
			btnGoToLogin = new JButton("Log In");
			btnGoToLogin.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					// CHANGE CONTENT PANE
					Client.this.setContentPane(getLogInContentPane());
					Client.this.validate();
					
				}
			});
			btnGoToLogin.setBounds(471, 346, 89, 23);
		}
		return btnGoToLogin;
	}
	///////////////////////////////////
	
	
	// LogIN items
	private JTextField getTextUsername() {
		if (textUsername == null) {
			textUsername = new JTextField();
			textUsername.setBounds(243, 94, 86, 20);
			textUsername.setColumns(10);
		}
		return textUsername;
	}
	private JTextField getTextPassword() {
		if (textPassword == null) {
			textPassword = new JTextField();
			textPassword.setColumns(10);
			textPassword.setBounds(243, 165, 86, 20);
		}
		return textPassword;
	}
	private JLabel getLblUsername() {
		if (lblUsername == null) {
			lblUsername = new JLabel("Username");
			lblUsername.setHorizontalAlignment(SwingConstants.CENTER);
			lblUsername.setBounds(10, 69, 550, 14);
		}
		return lblUsername;
	}
	private JLabel getLblPassword() {
		if (lblPassword == null) {
			lblPassword = new JLabel("Password");
			lblPassword.setHorizontalAlignment(SwingConstants.CENTER);
			lblPassword.setBounds(10, 140, 550, 14);
		}
		return lblPassword;
	}
	private JLabel getLblServerDown() {
		if (lblServerDown == null) {
			lblServerDown = new JLabel("Server is not currently working");
			lblServerDown.setFont(new Font("Tahoma", Font.PLAIN, 25));
			lblServerDown.setHorizontalAlignment(SwingConstants.CENTER);
			lblServerDown.setBounds(10, 146, 550, 42);
		}
		return lblServerDown;
	}
	private JLabel getLblLogIn() {
		if (lblLogIn == null) {
			lblLogIn = new JLabel("LOG IN");
			lblLogIn.setFont(new Font("Tahoma", Font.BOLD, 20));
			lblLogIn.setHorizontalAlignment(SwingConstants.CENTER);
			lblLogIn.setBounds(10, 11, 550, 33);
		}
		return lblLogIn;
	}
	private JLabel getLblNoAccount() {
		if (lblNoAccount == null) {
			lblNoAccount = new JLabel("No account?");
			lblNoAccount.setHorizontalAlignment(SwingConstants.CENTER);
			lblNoAccount.setBounds(471, 321, 89, 14);
		}
		return lblNoAccount;
	}
	private JTextField getTextShareLink() {
		if (textShareLink == null) {
			textShareLink = new JTextField();
			textShareLink.setBounds(243, 301, 89, 20);
			textShareLink.setColumns(10);
		}
		return textShareLink;
	}
	private JLabel getLblUseShareLink() {
		if (lblUseShareLink == null) {
			lblUseShareLink = new JLabel("Use share link");
			lblUseShareLink.setHorizontalAlignment(SwingConstants.CENTER);
			lblUseShareLink.setBounds(10, 276, 550, 14);
		}
		return lblUseShareLink;
	}
	
	
	// Register items
	private JTextField getTextRegPassword() {
		if (textRegPassword == null) {
			textRegPassword = new JTextField();
			textRegPassword.setColumns(10);
			textRegPassword.setBounds(243, 165, 86, 20);
		}
		return textRegPassword;
	}
	private JLabel getLabel() {
		if (label == null) {
			label = new JLabel("Password");
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setBounds(10, 140, 550, 14);
		}
		return label;
	}
	private JTextField getTextRegUsername() {
		if (textRegUsername == null) {
			textRegUsername = new JTextField();
			textRegUsername.setColumns(10);
			textRegUsername.setBounds(243, 94, 86, 20);
		}
		return textRegUsername;
	}
	private JLabel getLabel_1() {
		if (label_1 == null) {
			label_1 = new JLabel("Username");
			label_1.setHorizontalAlignment(SwingConstants.CENTER);
			label_1.setBounds(10, 69, 550, 14);
		}
		return label_1;
	}
	private JLabel getLblRegister() {
		if (lblRegister == null) {
			lblRegister = new JLabel("REGISTER");
			lblRegister.setHorizontalAlignment(SwingConstants.CENTER);
			lblRegister.setFont(new Font("Tahoma", Font.BOLD, 20));
			lblRegister.setBounds(10, 11, 550, 33);
		}
		return lblRegister;
	}
	private JLabel getLblHaveAccount() {
		if (lblHaveAccount == null) {
			lblHaveAccount = new JLabel("Have account?");
			lblHaveAccount.setFont(new Font("Tahoma", Font.PLAIN, 11));
			lblHaveAccount.setHorizontalAlignment(SwingConstants.CENTER);
			lblHaveAccount.setBounds(471, 321, 89, 14);
		}
		return lblHaveAccount;
	}
	
	
	// App items
	private JComboBox getComboBoxYourFilesTEST() {
		if (comboBoxYourFiles == null) {
			comboBoxYourFiles = new JComboBox();
			comboBoxYourFiles.setModel(new DefaultComboBoxModel(new String[] {"test1", "test2", "test3"}));
			comboBoxYourFiles.setToolTipText("");
			comboBoxYourFiles.setBounds(10, 70, 96, 20);
		}
		return comboBoxYourFiles;
	}
	private JComboBox getComboBoxSharedFromTEST() {
		if (comboBoxSharedFrom == null) {
			comboBoxSharedFrom = new JComboBox();
			comboBoxSharedFrom.setModel(new DefaultComboBoxModel(new String[] {"Select user"}));
			comboBoxSharedFrom.setToolTipText("");
			comboBoxSharedFrom.setBounds(10, 192, 121, 20);		
		}
		return comboBoxSharedFrom;
	}
	private JComboBox getComboBoxSharedToTEST() {
		if (comboBoxSharedTo == null) {
			comboBoxSharedTo = new JComboBox();
			comboBoxSharedTo.setModel(new DefaultComboBoxModel(new String[] {"Select user"}));
			comboBoxSharedTo.setToolTipText("");
			comboBoxSharedTo.setBounds(144, 252, 96, 20);
		}
		return comboBoxSharedTo;
	}
	private JLabel getLblWelcome() {
		if (lblWelcome == null) {
			lblWelcome = new JLabel("Welcome "+ currentUser.getUsername());
			lblWelcome.setFont(new Font("Tahoma", Font.PLAIN, 16));
			lblWelcome.setBounds(10, 11, 202, 14);
		}
		return lblWelcome;
	}
	private JLabel getLblYourFiles() {
		if (lblYourFiles == null) {
			lblYourFiles = new JLabel("Your files");
			lblYourFiles.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblYourFiles.setBounds(10, 47, 275, 14);
			
			if (currentUser.isPremium()) {
				lblYourFiles.setText("Selected folder: root");
			}
		}
		return lblYourFiles;
	}
	private JTextArea getTextArea() {
		if (textArea == null) {
			textArea = new JTextArea();
			textArea.setEditable(false);
		}
		return textArea;
	}
	private JLabel getLblSelectedFile() {
		if (lblSelectedFile == null) {
			lblSelectedFile = new JLabel("Selected file:");
			lblSelectedFile.setBounds(295, 62, 265, 14);
		}
		return lblSelectedFile;
	}
	private JLabel getLblUploadANew() {
		if (lblUploadANew == null) {
			lblUploadANew = new JLabel("Upload a new file");
			lblUploadANew.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblUploadANew.setBounds(10, 332, 134, 20);
		}
		return lblUploadANew;
	}
	private JLabel getLblEnterFilePath() {
		if (lblEnterFilePath == null) {
			lblEnterFilePath = new JLabel("Enter file path:");
			lblEnterFilePath.setBounds(10, 354, 134, 14);
		}
		return lblEnterFilePath;
	}
	private JTextField getTextFilePath() {
		if (textFilePath == null) {
			textFilePath = new JTextField();
			textFilePath.setBounds(10, 379, 134, 20);
			textFilePath.setColumns(10);
		}
		return textFilePath;
	}
	private JTextField getTextLink() {
		if (textLink == null) {
			textLink = new JTextField();
			
			if (currentUser.isLinkOn()) {
				textLink.setText(currentUser.getLink());
			}
			
			textLink.setEditable(false);
			textLink.setBounds(295, 31, 173, 20);
			textLink.setColumns(10);
		}
		return textLink;
	}
	private JLabel getLblLinkForSharing() {
		if (lblLinkForSharing == null) {
			lblLinkForSharing = new JLabel("Link for sharing");
			lblLinkForSharing.setBounds(295, 13, 265, 14);
		}
		return lblLinkForSharing;
	}
	private JLabel getLblWhoSharedWith() {
		if (lblWhoSharedWith == null) {
			lblWhoSharedWith = new JLabel("Who shared to you");
			lblWhoSharedWith.setBounds(10, 169, 121, 14);
		}
		return lblWhoSharedWith;
	}
	private JLabel getLblShareWith() {
		if (lblShareWith == null) {
			lblShareWith = new JLabel("Share to");
			lblShareWith.setBounds(10, 227, 69, 14);
		}
		return lblShareWith;
	}
	private JLabel getLblTheirFiles() {
		if (lblTheirFiles == null) {
			lblTheirFiles = new JLabel("Selected folder: root");
			lblTheirFiles.setBounds(144, 169, 141, 14);
		}
		return lblTheirFiles;
	}
	private JLabel getLblYouSharedTo() {
		if (lblYouSharedTo == null) {
			lblYouSharedTo = new JLabel("You shared to");
			lblYouSharedTo.setBounds(144, 227, 96, 14);
		}
		return lblYouSharedTo;
	}
	private JTextField getTextShareTo() {
		if (textShareTo == null) {
			textShareTo = new JTextField();
			textShareTo.setColumns(10);
			textShareTo.setBounds(10, 252, 121, 20);
		}
		return textShareTo;
	}
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setBounds(295, 87, 266, 312);
			scrollPane.setViewportView(getTextArea());
		}
		return scrollPane;
	}
	private JLabel getLblPremium() {
		if (lblPremium == null) {
			lblPremium = new JLabel("PREMIUM");
			lblPremium.setHorizontalAlignment(SwingConstants.CENTER);
			lblPremium.setBorder(new LineBorder(Color.RED, 2, true));
			lblPremium.setBounds(197, 13, 68, 14);
		}
		return lblPremium;
	}
	

}
