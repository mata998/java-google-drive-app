package client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import server.FileConvertor;

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

public class Client extends JFrame {

	// Login content pane
	private JPanel logInContentPane;
	private JButton btnLogIn;
	private JTextField textUsername;
	private JTextField textPassword;
	private JLabel lblUsername;
	private JLabel lblPassword;
	private JLabel lblServerDown;
	
	// App content pane
	private JPanel appContentPane;
	private JLabel lblPls;
	private JLabel lblNewLabel;
	private JComboBox comboBox;
	private JTextArea textArea;
	private JLabel lblSelectedFile;
	private JLabel lblUploadANew;
	private JLabel lblEnterFilePath;
	private JTextField textFilePath;
	private JButton btnUpload;
	
	// My things
	static final String ERROR_MSG = "***error";
	static final String SUCCESS_MSG = "***success";
	static final String FILE_REQUEST = "***file_request";
	static final String UPLOAD_REQUEST = "***upload_request";
	Socket connectionSocket = null;
	BufferedReader fromServerStream = null;
	PrintStream toServerStream = null;
	String serverMsg;
	String currentUsername = "mata998";
	String[] currentFiles = null;
	String currentFilesString;
	String requestedFileText = "";
	///////
	
	

	

	
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
		setBounds(100, 100, 450, 300);
		
		logInContentPane = new JPanel();
		logInContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(logInContentPane);
		logInContentPane.setLayout(null);
		
		
//		setContentPane(getAppContentPane());
		
		try {
			connectToServer();
			
			// LOG IN CONTENT PANE
			logInContentPane.add(getBtnLogIn());
			logInContentPane.add(getTextUsername());
			logInContentPane.add(getTextPassword());
			logInContentPane.add(getLblUsername());
			logInContentPane.add(getLblPassword());
			
			
			
		} catch (Exception e) {
			logInContentPane.add(getLblServerDown());
			
			System.out.println("SERVER DOWN");
		}
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
						
						// SENDING USS;PASS
						toServerStream.println(userName + ";" + password);
						
						// GETTING AUTHENTICATION
						serverMsg = fromServerStream.readLine();
						
						// LOG IN SUCCESSFUL ----------
						if (serverMsg.equals(SUCCESS_MSG)) {
							System.out.println("YAY");
							currentUsername = new String(userName);
							
							// GETTING FILE LIST
							serverMsg = fromServerStream.readLine();
							
							if (serverMsg.equals(ERROR_MSG)) {
								currentFilesString = "";
								currentFiles = null;
							}
							else {
								currentFilesString = serverMsg;
								currentFiles = serverMsg.split(";");
							}

							
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
						
						
					} catch (IOException e) {
						JOptionPane.showMessageDialog(
								Client.this,
								"Server stopped working", 
								"Error",
								JOptionPane.ERROR_MESSAGE);
						
						dispose();
					}
					
				}
			});
			btnLogIn.setBounds(168, 187, 89, 23);
		}
		return btnLogIn;
	}
	
	// APP CONTENT PANE
	private JPanel getAppContentPane() {
		if (appContentPane == null) {
			appContentPane = new JPanel();			
			appContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			appContentPane.setLayout(null);
			appContentPane.add(getLblPls());
			appContentPane.add(getLblNewLabel());
			appContentPane.add(getTextArea());
			appContentPane.add(getLblSelectedFile());
			appContentPane.add(getLblUploadANew());
			appContentPane.add(getLblEnterFilePath());
			appContentPane.add(getTextFilePath());
			appContentPane.add(getBtnUpload());
			
			if (currentFiles != null) {
				appContentPane.add(getComboBox(currentFiles));	
			}
			else {
				appContentPane.add(getComboBox(new String[] {"No files"}));
			}
			
		}
		return appContentPane;
	}

	// COMBOBOX ACTION
	private JComboBox getComboBox(String[] filesToShow) {
			if (comboBox == null) {
				comboBox = new JComboBox();
				comboBox.setModel(new DefaultComboBoxModel(filesToShow));
				comboBox.setToolTipText("");
				comboBox.setBounds(10, 77, 96, 20);
				
				
				comboBox.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						
						try {
							String selectedFile = (String) comboBox.getSelectedItem();
							
							if (currentFiles == null) {
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
			return comboBox;
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
					// FIX THIS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					
					if (currentFiles != null) {
						String[] newCurrentFiles = Arrays.copyOf(currentFiles, currentFiles.length+1);
						newCurrentFiles[newCurrentFiles.length-1] = fileName;
						
						currentFiles = newCurrentFiles;
						
						// UPDATE COMBO BOX
						comboBox.addItem(fileName);
					}
					else {
						currentFiles = new String[1];
						currentFiles[0] = fileName;
						
						// UPDATE COMBO BOX
						comboBox.insertItemAt(fileName, 0);
						comboBox.removeItemAt(1);
					}
					
					
					
					JOptionPane.showMessageDialog(
							Client.this,
							"File uploaded successfully!", 
							"Success",
							JOptionPane.INFORMATION_MESSAGE);
					
				}
			});
			btnUpload.setBounds(10, 218, 89, 23);
		}
		return btnUpload;
	}
	
	
	private JTextField getTextUsername() {
		if (textUsername == null) {
			textUsername = new JTextField();
			textUsername.setBounds(168, 54, 86, 20);
			textUsername.setColumns(10);
		}
		return textUsername;
	}
	private JTextField getTextPassword() {
		if (textPassword == null) {
			textPassword = new JTextField();
			textPassword.setColumns(10);
			textPassword.setBounds(168, 125, 86, 20);
		}
		return textPassword;
	}
	private JLabel getLblUsername() {
		if (lblUsername == null) {
			lblUsername = new JLabel("Username");
			lblUsername.setBounds(168, 29, 103, 14);
		}
		return lblUsername;
	}
	private JLabel getLblPassword() {
		if (lblPassword == null) {
			lblPassword = new JLabel("Password");
			lblPassword.setBounds(168, 100, 103, 14);
		}
		return lblPassword;
	}
	private JLabel getLblServerDown() {
		if (lblServerDown == null) {
			lblServerDown = new JLabel("Server is not currently working");
			lblServerDown.setHorizontalAlignment(SwingConstants.CENTER);
			lblServerDown.setBounds(10, 111, 414, 14);
		}
		return lblServerDown;
	}
	
	
	
	private JLabel getLblPls() {
		if (lblPls == null) {
			lblPls = new JLabel("Welcome "+ currentUsername);
			lblPls.setBounds(10, 22, 119, 14);
		}
		return lblPls;
	}
	private JLabel getLblNewLabel() {
		if (lblNewLabel == null) {
			lblNewLabel = new JLabel("Your files");
			lblNewLabel.setBounds(10, 55, 69, 14);
		}
		return lblNewLabel;
	}
	private JComboBox getComboBoxTestt() {
		if (comboBox == null) {
			comboBox = new JComboBox();
			comboBox.setEditable(true);
			comboBox.setModel(new DefaultComboBoxModel(new String[] {"test1", "test2", "test3"}));
			comboBox.setToolTipText("");
			comboBox.setBounds(10, 77, 96, 20);
		}
		return comboBox;
	}
	private JTextArea getTextArea() {
		if (textArea == null) {
			textArea = new JTextArea();
			textArea.setEditable(false);
			textArea.setBounds(191, 47, 233, 204);
		}
		return textArea;
	}
	private JLabel getLblSelectedFile() {
		if (lblSelectedFile == null) {
			lblSelectedFile = new JLabel("Selected file:");
			lblSelectedFile.setBounds(191, 22, 233, 14);
		}
		return lblSelectedFile;
	}
	private JLabel getLblUploadANew() {
		if (lblUploadANew == null) {
			lblUploadANew = new JLabel("Upload a new file");
			lblUploadANew.setBounds(10, 137, 134, 14);
		}
		return lblUploadANew;
	}
	private JLabel getLblEnterFilePath() {
		if (lblEnterFilePath == null) {
			lblEnterFilePath = new JLabel("Enter file path:");
			lblEnterFilePath.setBounds(10, 162, 134, 14);
		}
		return lblEnterFilePath;
	}
	private JTextField getTextFilePath() {
		if (textFilePath == null) {
			textFilePath = new JTextField();
			textFilePath.setBounds(10, 187, 134, 20);
			textFilePath.setColumns(10);
		}
		return textFilePath;
	}

}
