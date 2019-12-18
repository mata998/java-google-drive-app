package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
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
	Socket connectionSocket = null;
	BufferedReader fromClientStream = null;
	PrintStream toClientStream = null;
	ObjectOutputStream objectToClientStream = null;
	String clientMsg;
	User currentUser = null;

	

	
	// Constructor
	public ServerThread(Socket connectionSocket) {
		super();
		this.connectionSocket = connectionSocket;
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
					
					
					// AUTHENTICATION
					if (Server.authenticate(username, password)) {
						
						// inform client that it is succesfull
						toClientStream.println(SUCCESS_MSG);
						
						// get that User object
						currentUser = Server.getUser(username);
						
						// send User object to client
						objectToClientStream.writeObject(currentUser);
						
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
						String requestedFileText = FileConvertor.readFile(requestedFilePath);
						
						toClientStream.println(requestedFileText);
						toClientStream.println(SUCCESS_MSG);
					}
					
					
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
						
						String requestedFileText = FileConvertor.readFile(requestedFilePath);
						
						toClientStream.println(requestedFileText);
						toClientStream.println(SUCCESS_MSG);
					}
					
				}
				
				// UPLOAD REQUEST action /////////////////////////
				if (clientMsg.equals(UPLOAD_REQUEST)) {
					System.out.println("UPLOAD REQUEST");
					
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
				
				if (clientMsg.equals(LINKONCHANGE_REQUEST)) {
					System.out.println("LinkOn change request");
					
					// change LinkOn
					currentUser.setLinkOn(!currentUser.isLinkOn());
					
					Server.updateJsonUsers();
					
					toClientStream.println(SUCCESS_MSG);
				}
				
				if (clientMsg.equals(GETUSER_BY_SHARELINK)) {
					System.out.println("VISIT REQUEST");
					
					String shareLink = fromClientStream.readLine();
					
					System.out.println("Requested: " + shareLink);
					
					User user = Server.getUserByShareLink(shareLink);
					
					// User found
					if (user != null) {
						toClientStream.println(SUCCESS_MSG);
						
						// Set currentUser to chosen user
						currentUser = user;
						
						// Send chosen user to client
						objectToClientStream.writeObject(user);
					}
					else {
						toClientStream.println(ERROR_MSG);
						
						
					}
					
				}
				
				if (clientMsg.equals(LOGOUT_REQUEST)) {
					System.out.println("Logout request");
					System.out.println("Client dissconected");
					
					currentUser = null;
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
				
				
				
				
				
			} // end of App actions loop
			
			
		} catch (Exception e) {
			System.out.println("Client dissconected");
			
			Server.listOfThreads.remove(this);
		}
	}

}
