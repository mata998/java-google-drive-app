package client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.BufferedReader;
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

import server.FileConvertor;
import server.userClasses.User;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextArea;
import java.awt.Font;

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
	static final String LOGIN_REQUEST = "***login_request";
	static final String REGISTER_REQUEST = "***register_request";
	static final String FILE_REQUEST = "***file_request";
	static final String UPLOAD_REQUEST = "***upload_request";
	static final String LINKONCHANGE_REQUEST = "***linkchange_request";
	static final String GETUSER_BY_SHARELINK = "***getuserbysharelink";
	static final String LOGOUT_REQUEST = "***logout_request";
	Socket connectionSocket = null;
	BufferedReader fromServerStream = null;
	PrintStream toServerStream = null;
	ObjectInputStream objectFromServerStream = null;
	String serverMsg;
	User currentUser = new User();
	String requestedFileText = "";
	boolean itsVisit = false; ///////
	private JLabel lblHaveAccount;
	private JButton btnGoToLogin;
	private JButton btnLogOut;
	private JTextField textLink;
	private JButton btnLink;
	private JLabel lblLinkForSharing;
	private JButton btnDownload;
	private JLabel lblWhoSharedWith;
	private JComboBox comboBoxWhoShared;
	private JLabel lblShareWith;
	private JComboBox comboBoxSharedTo;
	private JComboBox comboBoxTheirFiles;
	private JLabel lblTheirFiles;
	private JLabel lblYouSharedTo;
	private JTextField textShareTo;
	private JTextField textShareLink;
	private JLabel lblUseShareLink;
	private JButton btnVisit;
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
		setBounds(430, 200, 586, 418);

		
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
			
			try {
				connectToServer();
				
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
				
				
			} catch (Exception e) {
				logInContentPane.add(getLblServerDown());
				
				System.out.println("SERVER DOWN");
			}
			
			
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
	
	// LOGIN BUTTON
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
							System.out.println("YAY");

							// Get User object
							currentUser = (User) objectFromServerStream.readObject();
							
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
	//////////////////////////
	
	
	// APP CONTENT PANE
	private JPanel getAppContentPane() {
		if (appContentPane == null) {
			appContentPane = new JPanel();			
			appContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			appContentPane.setLayout(null);
			appContentPane.add(getLblWelcome());
			appContentPane.add(getLblYourFiles());
			appContentPane.add(getTextArea());
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
			
			// new stuff
			appContentPane.add(getLblWhoSharedWith());
			appContentPane.add(getComboBoxWhoShared());
			appContentPane.add(getLblShareWith());
			appContentPane.add(getComboBoxSharedTo());
			appContentPane.add(getComboBoxTheirFiles());
			appContentPane.add(getLblTheirFiles());
			appContentPane.add(getLblYouSharedTo());
			appContentPane.add(getTextShareTo());
			
//			appContentPane.add(getComboBoxYourFilesTEST());
						
			if (currentUser.getFiles().isEmpty() == false) {
				appContentPane.add(getComboBoxYourFiles(currentUser.getFilesStringArr()));	
			}
			else {
				appContentPane.add(getComboBoxYourFiles(new String[] {"No files"}));
			}
			
			// REMOVE things that visiters cant do
			if (itsVisit) {
				lblWelcome.setText("Welcome to " + currentUser.getUsername() + "'s drive");
				lblYourFiles.setText("Files");
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
			}
			
		}
		return appContentPane;
	}

	// COMBOBOX ACTION
	private JComboBox getComboBoxYourFiles(String[] filesToShow) {
			if (comboBoxYourFiles == null) {
				comboBoxYourFiles = new JComboBox();
				comboBoxYourFiles.setModel(new DefaultComboBoxModel(filesToShow));
				comboBoxYourFiles.setToolTipText("");
				comboBoxYourFiles.setBounds(10, 85, 96, 20);
				
				
				comboBoxYourFiles.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						
						try {
							String selectedFile = (String) comboBoxYourFiles.getSelectedItem();
							
							if (currentUser.getFiles().isEmpty()) {
								return;
							}
							
							// Request a file from server
							toServerStream.println(FILE_REQUEST);
							toServerStream.println(selectedFile);
							
							System.out.println("REQUESTED: " + selectedFile);
							
							// Get file from server
							requestedFileText = "";
							String oneLine;
							
							while (true) {
								oneLine = fromServerStream.readLine();
								
								if (oneLine.equals(SUCCESS_MSG)) {
									break;
								}
								
								requestedFileText = requestedFileText + oneLine + "\n";
							}
							
							textArea.setText(requestedFileText);
							
							
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
			return comboBoxYourFiles;
		}
	
	// UPLOAD BTN ACTION
	private JButton getBtnUpload() {
		if (btnUpload == null) {
			btnUpload = new JButton("Upload!");
			btnUpload.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
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
					
					
//					if (currentFiles != null && currentFiles.length == 5) {
//						JOptionPane.showMessageDialog(
//								Client.this,
//								"You have 5 files, you can't upload more", 
//								"Error",
//								JOptionPane.ERROR_MESSAGE);
//						return;
//					}
					
					if (currentUser.getFiles().size() == 5) {
						JOptionPane.showMessageDialog(
								Client.this,
								"You have 5 files, you can't upload more", 
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					
					String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
					
					
					
					// SEND UPLOAD REQUEST
					toServerStream.println(UPLOAD_REQUEST);
					System.out.println("i wanna UPLOAD!!!");
					System.out.println(fileName);
					
					// SEND FILE NAME
					toServerStream.println(fileName);
					
					// SEND FILE TEXT
					toServerStream.println(fileText);
					toServerStream.println(SUCCESS_MSG);
					
					// UPDATE currentUser files
//					if (currentFiles != null) {
//						String[] newCurrentFiles = Arrays.copyOf(currentFiles, currentFiles.length+1);
//						newCurrentFiles[newCurrentFiles.length-1] = fileName;
//						
//						currentFiles = newCurrentFiles;
//						
//						// UPDATE COMBO BOX
//						comboBox.addItem(fileName);
//					}
//					else {
//						currentFiles = new String[1];
//						currentFiles[0] = fileName;
//						
//						// UPDATE COMBO BOX
//						comboBox.insertItemAt(fileName, 0);
//						comboBox.removeItemAt(1);
//					}
					if (currentUser.getFiles().isEmpty() == false) {
						
						currentUser.getFiles().add(fileName);
						
						// UPDATE COMBO BOX
						comboBoxYourFiles.addItem(fileName);
					}
					else {
						currentUser.getFiles().add(fileName);
						
						// UPDATE COMBO BOX
						comboBoxYourFiles.insertItemAt(fileName, 0);
						comboBoxYourFiles.removeItemAt(1);
					}
					
					
					
					JOptionPane.showMessageDialog(
							Client.this,
							"File uploaded successfully!", 
							"Success",
							JOptionPane.INFORMATION_MESSAGE);
					
				}
			});
			btnUpload.setBounds(10, 346, 89, 23);
		}
		return btnUpload;
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
	
	// REGISTER BUTTON
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
	
	// App items
	private JComboBox getComboBoxYourFilesTEST() {
		if (comboBoxYourFiles == null) {
			comboBoxYourFiles = new JComboBox();
			comboBoxYourFiles.setModel(new DefaultComboBoxModel(new String[] {"test1", "test2", "test3"}));
			comboBoxYourFiles.setToolTipText("");
			comboBoxYourFiles.setBounds(10, 85, 96, 20);
		}
		return comboBoxYourFiles;
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
			lblYourFiles.setBounds(10, 62, 69, 14);
		}
		return lblYourFiles;
	}
	private JTextArea getTextArea() {
		if (textArea == null) {
			textArea = new JTextArea();
			textArea.setEditable(false);
			textArea.setBounds(327, 87, 233, 248);
		}
		return textArea;
	}
	private JLabel getLblSelectedFile() {
		if (lblSelectedFile == null) {
			lblSelectedFile = new JLabel("Selected file:");
			lblSelectedFile.setBounds(327, 62, 233, 14);
		}
		return lblSelectedFile;
	}
	private JLabel getLblUploadANew() {
		if (lblUploadANew == null) {
			lblUploadANew = new JLabel("Upload a new file");
			lblUploadANew.setBounds(10, 265, 134, 14);
		}
		return lblUploadANew;
	}
	private JLabel getLblEnterFilePath() {
		if (lblEnterFilePath == null) {
			lblEnterFilePath = new JLabel("Enter file path:");
			lblEnterFilePath.setBounds(10, 290, 134, 14);
		}
		return lblEnterFilePath;
	}
	private JTextField getTextFilePath() {
		if (textFilePath == null) {
			textFilePath = new JTextField();
			textFilePath.setBounds(10, 315, 134, 20);
			textFilePath.setColumns(10);
		}
		return textFilePath;
	}
	private JButton getBtnLogOut() {
		if (btnLogOut == null) {
			btnLogOut = new JButton("Log out");
			btnLogOut.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						
						toServerStream.println(LOGOUT_REQUEST);
						
						// Restart vars
						currentUser = new User();
						itsVisit = false;

						lblWelcome = null;
						comboBoxYourFiles = null;
						comboBoxWhoShared = null;
						comboBoxTheirFiles = null;
						comboBoxSharedTo = null;
						textArea = null;
						textFilePath = null;
						textShareTo = null;
						textLink = null;
						btnLink = null;
						
						appContentPane = null;
						
						textUsername = null;
						textPassword = null;
						textShareLink = null;
						
						logInContentPane = null;
						
						textRegUsername = null;
						textRegPassword = null;
						
						registerContentPane = null;
						
						// CHANGE CONTENT PANE
						Client.this.setContentPane(getLogInContentPane());
						Client.this.validate();
					}
					catch (Exception ex) {
						// TODO: handle exception
					}
					
				}
			});
			btnLogOut.setBounds(471, 346, 89, 23);
		}
		return btnLogOut;
	}
	private JTextField getTextLink() {
		if (textLink == null) {
			textLink = new JTextField();
			
			if (currentUser.isLinkOn()) {
				textLink.setText(currentUser.getLink());
			}
			
			textLink.setEditable(false);
			textLink.setBounds(327, 31, 141, 20);
			textLink.setColumns(10);
		}
		return textLink;
	}
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
					
				}
			});
			btnLink.setBounds(471, 28, 89, 23);
		}
		return btnLink;
	}
	private JLabel getLblLinkForSharing() {
		if (lblLinkForSharing == null) {
			lblLinkForSharing = new JLabel("Link for sharing");
			lblLinkForSharing.setBounds(327, 13, 233, 14);
		}
		return lblLinkForSharing;
	}
	private JButton getBtnDownload() {
		if (btnDownload == null) {
			btnDownload = new JButton("Download");
			btnDownload.setBounds(327, 346, 100, 23);
		}
		return btnDownload;
	}
	private JLabel getLblWhoSharedWith() {
		if (lblWhoSharedWith == null) {
			lblWhoSharedWith = new JLabel("Who shared with you");
			lblWhoSharedWith.setBounds(10, 127, 121, 14);
		}
		return lblWhoSharedWith;
	}
	private JComboBox getComboBoxWhoShared() {
		if (comboBoxWhoShared == null) {
			comboBoxWhoShared = new JComboBox();
			comboBoxWhoShared.setToolTipText("");
			comboBoxWhoShared.setBounds(10, 150, 96, 20);
		}
		return comboBoxWhoShared;
	}
	private JLabel getLblShareWith() {
		if (lblShareWith == null) {
			lblShareWith = new JLabel("Share to");
			lblShareWith.setBounds(10, 198, 69, 14);
		}
		return lblShareWith;
	}
	private JComboBox getComboBoxSharedTo() {
		if (comboBoxSharedTo == null) {
			comboBoxSharedTo = new JComboBox();
			comboBoxSharedTo.setToolTipText("");
			comboBoxSharedTo.setBounds(144, 223, 96, 20);
		}
		return comboBoxSharedTo;
	}
	private JComboBox getComboBoxTheirFiles() {
		if (comboBoxTheirFiles == null) {
			comboBoxTheirFiles = new JComboBox();
			comboBoxTheirFiles.setToolTipText("");
			comboBoxTheirFiles.setBounds(144, 150, 96, 20);
		}
		return comboBoxTheirFiles;
	}
	private JLabel getLblTheirFiles() {
		if (lblTheirFiles == null) {
			lblTheirFiles = new JLabel("Their files");
			lblTheirFiles.setBounds(144, 127, 121, 14);
		}
		return lblTheirFiles;
	}
	private JLabel getLblYouSharedTo() {
		if (lblYouSharedTo == null) {
			lblYouSharedTo = new JLabel("You shared to");
			lblYouSharedTo.setBounds(144, 198, 96, 14);
		}
		return lblYouSharedTo;
	}
	private JTextField getTextShareTo() {
		if (textShareTo == null) {
			textShareTo = new JTextField();
			textShareTo.setColumns(10);
			textShareTo.setBounds(10, 223, 121, 20);
		}
		return textShareTo;
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
	private JButton getBtnVisit() {
		if (btnVisit == null) {
			btnVisit = new JButton("Visit");
			btnVisit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						String shareLink = textShareLink.getText();
						
						toServerStream.println(GETUSER_BY_SHARELINK);
						
						toServerStream.println(shareLink);
						
						serverMsg = fromServerStream.readLine();
						
						if (serverMsg.equals(SUCCESS_MSG)) {
							
							currentUser = (User) objectFromServerStream.readObject();
							itsVisit = true;
							
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
}
