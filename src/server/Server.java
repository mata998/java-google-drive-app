package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import server.userClasses.User;


public class Server {

	public static List<ServerThread> listOfThreads = new LinkedList<>();
	public static List<User> listOfUsers = null;
	
	public static void main(String[] args) {
		System.out.println("SERVER\n");
		
		loadJsonUsers();
		
		try {
			// connecting server
			ServerSocket serverSocket = new ServerSocket(3000);
			System.out.println("Server is running...");
			
			// transfer server
			ServerSocket transferserverSocket = new ServerSocket(3001);
			
			Socket connectionSocket = null;
			
			while (true) {
				connectionSocket = serverSocket.accept();
				System.out.println("\nClient connected!");
				
				ServerThread newClient = new ServerThread(connectionSocket, transferserverSocket);
				
				listOfThreads.add(newClient);

				newClient.start();
			}
			
			
		} catch (IOException e) {
			System.out.println("ERROR");
//			e.printStackTrace();
		}

	}
	
	
	public static void loadJsonUsers() {
		
		String json = FileConvertor.readFile("src/server/users.json");
		
		listOfUsers = FileConvertor.jsonToUsers(json);
		
	}
	
	public static void updateJsonUsers() {
		
		String json = FileConvertor.usersToJson(listOfUsers);
		
		FileConvertor.textToFile(json, "src/server/users.json");
		
	}
	
	public static void printUsers(List<User> list) {
		System.out.println("\nUsers:");
		
		for (User user : list) {
//			System.out.println(" " + user.getUsername());
			user.showAll();
		}
		
		System.out.println();
	}

	public static boolean authenticate(String username, String password) {
		
		for (User user : listOfUsers) {
			
			if (user.getUsername().equals(username) &&
				user.getPassword().equals(password))
			{
				return true;
			}
			
		}
		
		return false;
	}
	
	public static boolean userExists(String username) {
		
		for (User user : listOfUsers) {
			
			if (user.getUsername().equals(username)) {
				return true;
			}
			
		}
		
		return false;
	}
	
	public static User getUser(String username) {
		
		for (User user : listOfUsers) {
			
			if (user.getUsername().equals(username)) {
				return user;
			}
			
		}
		
		return null;
	}
	
	public static User getUserByShareLink(String shareLink) {
		
		for (User user : listOfUsers) {
			
			if (user.getLink().equals(shareLink) && user.isLinkOn()) {
				return user;
			}
			
		}
		
		return null;
	}
	
	public static String getFilesFromUserSemiCol(String targetUsername) {
		
		for (User user : listOfUsers) {
			if (user.getUsername().equals(targetUsername)) {
				
				return user.getFilesSemiColon();
			}
			
		}
		
		return null;
	}
	
	
	
}
