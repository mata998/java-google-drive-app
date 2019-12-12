package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;

import server.userClasses.User;

public class ServerThread extends Thread{
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
					String requestedFile = new String(clientMsg);
					System.out.println("CLIENT REQUESTED: " + requestedFile);
					
					String requestedFileText = FileConvertor.readFile(userDirectory + requestedFile);
					
					toClientStream.println(requestedFileText);
					toClientStream.println(SUCCESS_MSG);
				}
				
				
				// UPLOAT REQUEST action /////////////////////////
				if (clientMsg.equals(UPLOAD_REQUEST)) {
					System.out.println("UPLOAD REQUEST");
					
					// GET NEW FILE NAME
					clientMsg = fromClientStream.readLine();
					String newFileName = new String(clientMsg);
					System.out.println("client wants to upload: " + newFileName);
					
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
					String newFileDirectory = "src/server/database/"+currentUser.getUsername() + "/";
					
					FileConvertor.textToFile(newFileText, newFileDirectory + newFileName);
					System.out.println("FILE CREATED");
					
					// ADD IT TO currentUser
					currentUser.addFile(newFileName);
					
					// UPDATE JSON DATABASE
					Server.updateJsonUsers();
					System.out.println("DATABASE UPDATED");
					
				}
				
				if (clientMsg.equals(LINKONCHANGE_REQUEST)) {
					System.out.println("LinkOn change request");
					
					// change LinkOn
					currentUser.setLinkOn(!currentUser.isLinkOn());
					
					Server.updateJsonUsers();
				}
				
				if (clientMsg.equals(GETUSER_BY_SHARELINK)) {
					System.out.println("VISIT REQUEST");
					
					String shareLink = fromClientStream.readLine();
					
					User user = Server.getUserByShareLink(shareLink);
					
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
				
				
			} // end of App actions loop
			
			
		} catch (Exception e) {
			System.out.println("Client dissconected");
			
			Server.listOfThreads.remove(this);
		}
	}

}
