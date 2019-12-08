package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ServerThread extends Thread{
	static final String ERROR_MSG = "***error";
	static final String SUCCESS_MSG = "***success";
	static final String FILE_REQUEST = "***file_request";
	static final String UPLOAD_REQUEST = "***upload_request";
	Socket connectionSocket = null;
	BufferedReader fromClientStream = null;
	PrintStream toClientStream = null;
	String clientMsg;
	User currentUser = null;
	String requestedFile = null;
	String requestedFileText = null;
	String newFileName = null;
	String newFileText = null;
	

	
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
		
			// LOGIN loop
			while (true) {
				System.out.println("waiting for uss and pass...");
				
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
					
					// send client file list
					if (currentUser.getFiles().isEmpty() == false) {
						toClientStream.println(currentUser.getFilesString());
					}
					else {
						toClientStream.println(ERROR_MSG);
					}
					
					// END LOGIN loop
					break;
						
				}
				else {
					toClientStream.println(ERROR_MSG);
				}
				
			} // end of LOGIN loop
			
			
			while (true) {
				// wait for next action
				System.out.println("\nWaiting for actions");
				clientMsg = fromClientStream.readLine();
				
				
				if (clientMsg.equals(FILE_REQUEST)) {
					String userDirectory = "src/server/database/" + currentUser.getUsername() + "/";
					
					// getting file request
					clientMsg = fromClientStream.readLine();
					requestedFile = new String(clientMsg);
					System.out.println("CLIENT REQUESTED: " + requestedFile);
					
					requestedFileText = FileConvertor.readFile(userDirectory + requestedFile);
					
					toClientStream.println(requestedFileText);
					toClientStream.println(SUCCESS_MSG);
				}
				
				
				if (clientMsg.equals(UPLOAD_REQUEST)) {
					System.out.println("he wants to push it");
					
					// GET NEW FILE NAME
					clientMsg = fromClientStream.readLine();
					newFileName = new String(clientMsg);
					System.out.println("he wants to push: " + newFileName);
					
					// GET NEW FILE TEXT
					newFileText = "";
					String oneLine;
					
					while (true) {
						oneLine = fromClientStream.readLine();
						
						if (oneLine.equals(SUCCESS_MSG)) {
							break;
						}
						
						newFileText = newFileText + oneLine + "\n";
					}
					
					System.out.println("FILE RECIVED: ");
					System.out.println(newFileText);
					
					
					// MAKE THAT FILE
					String newFileDirectory = "src/server/database/"+currentUser.getUsername() + "/";
					
					FileConvertor.textToFile(newFileText, newFileDirectory + newFileName);
					System.out.println("FILE CREATED");
					
					// ADD IT TO currentUser
					currentUser.addFile(newFileName);
					
					// UPDATE JSON DATABASE
					Server.updateUsers();
					
					
				}
				
				
				
			}
			
			
		} catch (Exception e) {
			System.out.println("Client dissconected");
		}
	}

}
