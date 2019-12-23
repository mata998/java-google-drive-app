package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.List;

import server.userClasses.User;

public class ServerThread extends Thread{
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
	static final String MOVE_TO = "***moveto";
	static final String MOVE_BACK = "***moveback";
	static final String FILES_IN_FOLDER = "***filesinfolder";
	static final String ITS_TXT_FILE = "***itstxt";
	static final String ITS_BIN_FILE = "***itsbin";
	static final String DOWNLOAD_BIN = "***downloadbin";
	Socket connectionSocket = null;
	BufferedReader fromClientStream = null;
	PrintStream toClientStream = null;
	ObjectOutputStream objectToClientStream = null;
	String clientMsg;
	User currentUser = null;
	ServerSocket transferServerSocket=null;
	InputStream is = null;
	OutputStream os = null;
	

	
	// Constructor
	public ServerThread(Socket connectionSocket, ServerSocket transferServerSocket) {
		super();
		this.connectionSocket = connectionSocket;
		this.transferServerSocket = transferServerSocket;
	}

	
	@Override
	public void run() {
		try {
			System.out.println("New server thread started!");
			
			fromClientStream = 
					new BufferedReader( 
						new InputStreamReader(
							connectionSocket.getInputStream()));

			toClientStream = 
					new PrintStream(
						connectionSocket.getOutputStream());
			
			
			objectToClientStream = new ObjectOutputStream(connectionSocket.getOutputStream());
			
			
			
			
			// APP ACTIONS LOOP
			while (true) {
				
				// wait for next action
				System.out.println("\nWaiting for actions");
				clientMsg = fromClientStream.readLine();
				
				
				// LOGIN action /////////////////////////
				if (clientMsg.equals(LOGIN_REQUEST)) {
					System.out.println("LOGIN REQUEST");
					
					// Getting ussername;password   string
					clientMsg = fromClientStream.readLine();
					
					String[] usspass = clientMsg.split(";");
					String username = usspass[0];
					String password = usspass[1];
					
					System.out.println("Wants to login: " + username);
					
					// AUTHENTICATION
					if (Server.authenticate(username, password)) {
						
						// inform client that it is succesfull
						toClientStream.println(SUCCESS_MSG);
						
						// get that User object
						currentUser = Server.getUser(username);
						System.out.println("Sending obj");
						
						// w8 to be sure it syncs with readObj
						Thread.sleep(200);
						
						currentUser.showAll();
						// send User object to client
						objectToClientStream.writeObject(currentUser);
						objectToClientStream.flush();
						
						// send is link on????
						if (currentUser.isLinkOn()) {
							toClientStream.println(SUCCESS_MSG);
						}
						else {
							toClientStream.println(ERROR_MSG);
						}
						
						System.out.println("Obj sent");
						
					}
					else {
						toClientStream.println(ERROR_MSG);
					}
					
				}
				
				
				// REGISTER action /////////////////////////////////
				if (clientMsg.equals(REGISTER_REQUEST)) {
					System.out.println("REGISTER REQUEST");
					 
					// Getting ussername;password   string
					clientMsg = fromClientStream.readLine();
					
					String[] usspass = clientMsg.split(";");
					String username = usspass[0];
					String password = usspass[1];
					
					
					if (Server.userExists(username) == false) {
										
						// Create new user
						currentUser = new User();
						currentUser.setUsername(username);
						currentUser.setPassword(password);
						currentUser.setLink("superdrive.com/visit/" + username);
						
						// CREATE PREMIUM USER
						if (password.startsWith("!")) {
							currentUser.setPremium(true);
						}
						
						// Add it to list of users
						Server.listOfUsers.add(currentUser);
						
						// Update json database
						Server.updateJsonUsers();
						
						// Create database folder
						File newfolder = new File("src/server/database/" + currentUser.getUsername());
						newfolder.mkdir();
						

						// INFORM CLIENT about successfull registration
						toClientStream.println(SUCCESS_MSG);
						
						// Send User object to client
						objectToClientStream.writeObject(currentUser);
						
					}
					else {
						
						toClientStream.println(ERROR_MSG);
					
					}
					
					
				}
				
				
				// FILE REQUEST action /////////////////////////
				if (clientMsg.equals(FILE_REQUEST)) {
					System.out.println("FILE REQUEST");
					
					String userDirectory = "src/server/database/" + currentUser.getUsername() + "/";
					
					// getting file request
					clientMsg = fromClientStream.readLine();
					String requestedFileName = new String(clientMsg);
					System.out.println("CLIENT REQUESTED: " + requestedFileName);
					
					String requestedFilePath = userDirectory + requestedFileName;
					
					File file = new File(requestedFilePath);
					
					// Check if its directory
					if (file.isDirectory()) {
						toClientStream.println(ITS_DIRECTORY);
						System.out.println("Requested directory: " + requestedFileName);
						
						
						String filesInFolder = "";
						File[] listOfFiles = file.listFiles();
						
						// Check if its empty
						if (listOfFiles.length == 0) {
							toClientStream.println(ERROR_MSG);

						}
						else { // If its not, send files in dir
							toClientStream.println(SUCCESS_MSG);
							
							for (File x : listOfFiles) {
								filesInFolder = filesInFolder + x.getName() + ";";
							}
							
							toClientStream.println(filesInFolder);
						}
						
						
					}
					else { // If its a file, send file content
						toClientStream.println(SUCCESS_MSG);
						
						
						// if its .txt file
						if (requestedFilePath.endsWith(".txt")) {
							toClientStream.println(ITS_TXT_FILE);
							
							String requestedFileText = FileConvertor.readFile(requestedFilePath);
							
							toClientStream.println(requestedFileText);
							toClientStream.println(SUCCESS_MSG);
						
						}
						else { // if its bin
							toClientStream.println(ITS_BIN_FILE);
							System.out.println("SEND BINARY");
							
							
					        
						}
						
					}
					
					
				}
				
				
				if (clientMsg.equals(DOWNLOAD_BIN)) {
					System.out.println("DOWNLOAD BIN REQUEST");
					
					
					// get file path
					clientMsg = fromClientStream.readLine();
					String requestedFilePath = new String(clientMsg);
					
					// get selectedUser
					clientMsg = fromClientStream.readLine();
					String selectedUser = new String(clientMsg);
					
					String userDirectory = "src/server/database/" + selectedUser + "/";
					
					
					String requestedFileFullPath = userDirectory + requestedFilePath;
					
					System.out.println("Requested: " + requestedFileFullPath);
					
					//////////////////////////////////////////////////////////////
										
					// from file stream
					File fileToSend = new File(requestedFileFullPath);
					FileInputStream fis = new FileInputStream(fileToSend);
					BufferedInputStream bis = new BufferedInputStream(fis); 
					
					// to client byte stream
					os = connectionSocket.getOutputStream();
					
					
					//Read File Contents into contents array 
					byte[] contents;
			        long fileLength = fileToSend.length(); 
			        long current = 0;
			         
			        
			        
			        Socket transferSokcet = transferServerSocket.accept();
			        os = transferSokcet.getOutputStream();
			        
			        System.out.println("Sending file...");
			        while(current!=fileLength){ 
			            int size = 5000;
			            if(fileLength - current >= size)
			                current += size;    
			            else{ 
			                size = (int)(fileLength - current); 
			                current = fileLength;
			            } 
			            contents = new byte[size]; 
			            bis.read(contents, 0, size); 
			            os.write(contents);
//			            System.out.println("Sending file ... "+(current*100)/fileLength+"% complete!");
			            System.out.println("Current: " + current);
			        }   
			        
			        
			        os.flush(); 
					
			        //File transfer done
					bis.close();
					fis.close();
					
					transferSokcet.close();
					
					
					
					System.out.println("FILE SENT");
				}
				
				if (clientMsg.equals(FILE_FROM_USER_REQUEST)) {
					System.out.println("FILE FROM USER REQUEST");
					
					// getting file request
					clientMsg = fromClientStream.readLine();
					String requestedFile = clientMsg;
					
					clientMsg = fromClientStream.readLine();
					String requestedUser = clientMsg;
					
					String requestedFilePath = "src/server/database/" + requestedUser + "/" + requestedFile;
					
					
					System.out.println("CLIENT REQUESTED: " + requestedFile + " FROM: " + requestedUser);
					
					
					File file = new File(requestedFilePath);
					
					if (file.isDirectory()) {
						toClientStream.println(ITS_DIRECTORY);
						
						String filesInFolder = "";
						File[] listOfFiles = file.listFiles();
						
						// Check if its empty
						if (listOfFiles.length == 0) {
							toClientStream.println(ERROR_MSG);

						}
						else {
							toClientStream.println(SUCCESS_MSG);
							
							for (File x : listOfFiles) {
								filesInFolder = filesInFolder + x.getName() + ";";
							}
							
							toClientStream.println(filesInFolder);
						}
						
					}
					else {
						toClientStream.println(SUCCESS_MSG);
						
						// if its .txt file
						if (requestedFilePath.endsWith(".txt")) {
							toClientStream.println(ITS_TXT_FILE);
							System.out.println("Send that its txt");
							
							String requestedFileText = FileConvertor.readFile(requestedFilePath);
							
							toClientStream.println(requestedFileText);
							toClientStream.println(SUCCESS_MSG);
						
							
						} // if its BIN file
						else {
							toClientStream.println(ITS_BIN_FILE);
							
							System.out.println("Send that its BIN");
							
						}
					
						
					}
					
				}
				
				// UPLOAD REQUEST action /////////////////////////
				if (clientMsg.equals(UPLOAD_REQUEST)) {
					System.out.println("UPLOAD REQUEST");
					
					
					// check if its TXT or BIN
					clientMsg = fromClientStream.readLine();
					if (clientMsg.equals(ITS_TXT_FILE)) {
						// GET NEW FILE NAME
						clientMsg = fromClientStream.readLine();
						String newFileName = new String(clientMsg);
						System.out.println("client wants to upload: " + newFileName);
						
						// GET FILE PATH
						clientMsg = fromClientStream.readLine();
						String newFilePath = new String(clientMsg);
						
						// GET NEW FILE TEXT
						String newFileText = "";
						String oneLine;
						
						while (true) {
							oneLine = fromClientStream.readLine();
							
							if (oneLine.equals(SUCCESS_MSG)) {
								break;
							}
							
							newFileText = newFileText + oneLine + "\n";
						}
						
						System.out.println("FILE RECIVED");
						
						
						// MAKE THAT FILE
						String newFileDirectory = "src/server/database/"+currentUser.getUsername() + "/" + newFilePath;
						
						FileConvertor.textToFile(newFileText, newFileDirectory + newFileName);
						
						
						
						System.out.println("FILE CREATED");
						
						
						// update database only if file is not added to folder
						if (newFilePath.equals("")) {
							// ADD IT TO currentUser
							currentUser.addFile(newFileName);
							
							// UPDATE JSON DATABASE
							Server.updateJsonUsers();
							System.out.println("DATABASE UPDATED");
						}
						
						
						// inform client that its ok
						toClientStream.println(SUCCESS_MSG);
					}
					else {
						// GET NEW FILE NAME
						clientMsg = fromClientStream.readLine();
						String newFileName = new String(clientMsg);
						System.out.println("client wants to upload: " + newFileName);
						
						// GET FILE PATH
						clientMsg = fromClientStream.readLine();
						String newFilePath = new String(clientMsg);
						
						String newFileDirectory = "src/server/database/"+currentUser.getUsername() + "/" + newFilePath;
						
						
						////////////
						byte[] contents = new byte[5000];
	                    
	                    //Initialize the FileOutputStream to the output file's full path.
	                    FileOutputStream fos = new FileOutputStream(newFileDirectory + newFileName);
	                    BufferedOutputStream bos = new BufferedOutputStream(fos);
	                    
	                    
	                    long fileLength;
	                    long current = 0;
	                    
	                    
	                    //No of bytes read in one read() call
	                    int bytesRead = 0; 
	                    
	                    System.out.println("Getting file..");
	                    
	                    Socket transferSocket = transferServerSocket.accept();
	                    
	                    is = transferSocket.getInputStream();
	                    
	                    // Reading and writing
	                    while((bytesRead= is.read(contents)) != -1 ) {
	                    	
	                    	
	                        bos.write(contents, 0, bytesRead); 
	                        
	                        current += bytesRead;
	                        
	                        System.out.println("Current: " + current);
	                    }
	                    
	                    bos.flush();
	                    
	                    fos.close();
	                    bos.close();
	                    
	                    transferSocket.close();
	                    
	                    System.out.println("GOT FILE");
	                    
	                    
	                    System.out.println("FILE CREATED");
						
						
						// update database only if file is not added to folder
						if (newFilePath.equals("")) {
							// ADD IT TO currentUser
							currentUser.addFile(newFileName);
							
							// UPDATE JSON DATABASE
							Server.updateJsonUsers();
							System.out.println("DATABASE UPDATED");
						}
						
						
						// inform client that its ok
						toClientStream.println(SUCCESS_MSG);
					}
					
					
					
					
					
				}
				
				if (clientMsg.equals(LINKONCHANGE_REQUEST)) {
					System.out.println("LinkOn change request");
					
					// change LinkOn
					currentUser.setLinkOn(!currentUser.isLinkOn());
					
					Server.updateJsonUsers();
					
					Server.printUsers(Server.listOfUsers);
					
					toClientStream.println(SUCCESS_MSG);
				}
				
				if (clientMsg.equals(GETUSER_BY_SHARELINK)) {
					System.out.println("\nVISIT REQUEST");
					
					String shareLink = fromClientStream.readLine();
					
					System.out.println("Requested: " + shareLink);
					
					User user = Server.getUserByShareLink(shareLink);
					
					// User found
					if (user != null) {
						toClientStream.println(SUCCESS_MSG);
						
						System.out.println("User found, sending obj");
						
						// Set currentUser to chosen user
						currentUser = user;
						
						// w8 to be sure it syncs with readObj
						Thread.sleep(200);
						
						// Send chosen user to client
						objectToClientStream.writeObject(user);
						
						
						System.out.println("Obj sent");
					}
					else {
						toClientStream.println(ERROR_MSG);
						
						
					}
					
				}
				
				if (clientMsg.equals(LOGOUT_REQUEST)) {
					System.out.println("Logout request");
					System.out.println("Client dissconected");
					
					currentUser = null;
					
					Server.printUsers(Server.listOfUsers);
				}
				
				if (clientMsg.equals(SHARE_TO_REQUEST)) {
					System.out.println("Share to request");
					
					// get target username from user
					clientMsg = fromClientStream.readLine();
					
					String targetUsername = clientMsg;
					
					if (Server.userExists(targetUsername)) {
						toClientStream.println(SUCCESS_MSG);
						
						User targetUser = Server.getUser(targetUsername);
						
						targetUser.addSharedFromUser(currentUser.getUsername());
						
						currentUser.addSharedToUser(targetUsername);
						
						Server.updateJsonUsers();
						
						// inform about succesfull share
						toClientStream.println(SUCCESS_MSG);
						
						
						
						
					}
					else {
						toClientStream.println(ERROR_MSG);
					}
					
				}
				
				if (clientMsg.equals(GETFILES_USER_REQUEST)) {
					System.out.println("Get files for user request");
					
					String targetUsername;
					String filesSemicol;
					
					// Get target username from client
					clientMsg = fromClientStream.readLine();
					targetUsername = clientMsg;
					
					System.out.println("REQUESTED FOR: "  + targetUsername);
					
					// Get files
					filesSemicol = Server.getFilesFromUserSemiCol(targetUsername);
					
					// Send files to client
					if (filesSemicol != null) {
						toClientStream.println(filesSemicol);
					}
					else {
						toClientStream.println(ERROR_MSG);
					}
					
				}
				
				if (clientMsg.equals(CREATE_FOLDER_REQUEST)) {
					System.out.println("Create folder request");
					
					// Get new folder path
					clientMsg = fromClientStream.readLine();
					
					String newFolderPath = new String(clientMsg);
					String thisUserPath = "src/server/database/" + currentUser.getUsername() + "/";
					
					File newFolder = new File(thisUserPath + newFolderPath);
					
					System.out.println("New folder full path: " + thisUserPath + newFolderPath);
					
					// Check if that folder already exists
					if (newFolder.exists() == false) {
						toClientStream.println(SUCCESS_MSG);
						
						// Make folder
						if (newFolder.mkdir()) {
							
							// If its root dir
							if (newFolderPath.contains("/") == false) {
								
								String newFolderName = newFolder.getName();
								
								System.out.println("FOLDER NAME: " + newFolderName);
								
								// Update user and database
								currentUser.addFile(newFolderName);
								Server.updateJsonUsers();
								
								
							}
							
							
							System.out.println("Folder made!");
							toClientStream.println(SUCCESS_MSG);
							
						}
						else {
							toClientStream.println(ERROR_MSG);
						}
						
						
					}
					else { // that folder exists
						toClientStream.println(ERROR_MSG);
						
					}
					
				}
				
				
				if (clientMsg.equals(DELETE_FOLDER_REQUEST)) {
					
					// Get folder path
					clientMsg = fromClientStream.readLine();
					
					String folderPath = new String(clientMsg);
					String thisUserPath = "src/server/database/" + currentUser.getUsername() + "/";
					
					File folder = new File(thisUserPath + folderPath);
					
					if (folder.exists()) {
						System.out.println("User wants to delete this and it will be ok: " + thisUserPath + folderPath);
						
						// delete folder
						if (folder.delete()) {
							
							// if its in root
							if (folderPath.indexOf("/") == folderPath.lastIndexOf("/")) {
								
								// update database
								currentUser.removeFile(folderPath.substring(0,folderPath.length()-1));
								Server.updateJsonUsers();
								
							}
							
							
							toClientStream.println(SUCCESS_MSG);
						}
						else {
							toClientStream.println(ERROR_MSG);
						}
					}
					
					
				}
				
				
				if (clientMsg.equals(RENAME_FOLDER_REQUEST)) {
					System.out.println("Rename folder request" );
					
					// get targeted folder
					clientMsg = fromClientStream.readLine();
					String folderPath = new String(clientMsg);
					
					// get new folder name
					clientMsg = fromClientStream.readLine();
					String newFolderName = new String(clientMsg);
					
					
					String thisUserPath = "src/server/database/" + currentUser.getUsername() + "/";
					
					System.out.println("He wants to rename:");
					System.out.println(folderPath);
					System.out.println("to:");
					System.out.println(newFolderName);
					
					
					File folder = new File(thisUserPath+folderPath);
					
					File newFolder = new File(folder.getParent() + "/" + newFolderName);
					
					
					
					if (folder.renameTo(newFolder)) {
						
						// if its in root
						if (folderPath.contains("/") == false) {
							
							// update database
							currentUser.renameFile(folderPath, newFolderName);
							Server.updateJsonUsers();
							
						}
						
						
						toClientStream.println(SUCCESS_MSG);
					}
					else {
						toClientStream.println(ERROR_MSG);
					}
					
				}
				
				
				if (clientMsg.equals(MOVE_FILE_REQUEST)) {
					System.out.println("Move request");
					
					String userDirectory = "src/server/database/" + currentUser.getUsername() + "/";
					String fromPath;
					String destinationPath;
					
					// check if its move to or move back
					clientMsg = fromClientStream.readLine();
					if (clientMsg.equals(MOVE_TO)) {
						
						// get from path
						clientMsg = fromClientStream.readLine();
						fromPath = new String(clientMsg);
						
						// get destinationPath
						clientMsg = fromClientStream.readLine();
						destinationPath = new String(clientMsg);

						File oldFile = new File(userDirectory + fromPath);
								
						File newFile = new File(userDirectory + destinationPath + oldFile.getName());
						
						
						// move file
						if (oldFile.renameTo(newFile)) {
							toClientStream.println(SUCCESS_MSG);
							
							// if its in root
							if (fromPath.contains("/") == false) {
								
								// update database
								currentUser.removeFile(fromPath);
								Server.updateJsonUsers();
								
							}
							
							
						}
						else {
							toClientStream.print(ERROR_MSG);
							
						}
						
					} // if its move back
					else if (clientMsg.equals(MOVE_BACK)) {
						
						
						// client sends file path if its not in root
						clientMsg = fromClientStream.readLine();
						if (clientMsg.equals(ERROR_MSG) == false) {
							
							fromPath = new String(clientMsg);
							
							File file = new File(userDirectory + fromPath);
							
							String newPath = (new File(file.getParent()).getParent()) + "/" + file.getName(); 
							
							File newFile = new File(newPath);
							
							if  (file.renameTo(newFile)) {
								
								
								// if it moved to root
								if (fromPath.indexOf("/") == fromPath.lastIndexOf("/")) {
									
									// update database
									currentUser.addFile(newFile.getName());
									Server.updateJsonUsers();
									
									System.out.println("Added: " + newFile.getName());
								}
								
								// inform that it was successful
								toClientStream.println(SUCCESS_MSG);
							}
							else {
								
								toClientStream.println(ERROR_MSG);
							}
							
						}
						
						
					}
					
					
					
					
				}
				
				
				if (clientMsg.equals(FILES_IN_FOLDER)) {
					System.out.println("Dirs in folder request");
					
					String userDirectory = "src/server/database/" + currentUser.getUsername() + "/";
					
					// getting folder path
					clientMsg = fromClientStream.readLine();
					String requestedFolderName = new String(clientMsg);
					System.out.println("CLIENT REQUESTED: " + requestedFolderName);
					
					String requestedFolderPath = userDirectory + requestedFolderName;
					
					File folder = new File(requestedFolderPath);
					
					// Check if its directory
					if (folder.isDirectory()) {
						toClientStream.println(SUCCESS_MSG);
						
						
						
						String filesInFolder = "";
						File[] listOfFiles = folder.listFiles();
						
						
						// take all directories
						for (File x : listOfFiles) {
							
							if (x.isDirectory()) {
								
								filesInFolder = filesInFolder + x.getName() + ";";
							}
							
						}
						
						// if there are no dirs
						if (filesInFolder.equals("")) {
							toClientStream.println(ERROR_MSG);
							
						}
						else { // Send dirs
							toClientStream.println(SUCCESS_MSG);
							
							
							toClientStream.println(filesInFolder);
							System.out.println("Sent: " + filesInFolder);
						}
						
						
						
					}
					else {
						toClientStream.println(ERROR_MSG);
					}
					
					
				}
				
				
			} // end of App actions loop
			
			
		} catch (Exception e) {
//			e.printStackTrace();
			System.out.println("Client dissconected");
			
			Server.listOfThreads.remove(this);
		}
	}

}
