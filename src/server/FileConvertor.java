package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import server.userClasses.User;


public class FileConvertor {
	
	public static void textToFile(String text, String fileName) {
		
		try (FileWriter fw =new FileWriter(fileName);
			 BufferedWriter bw = new BufferedWriter(fw);
			 PrintWriter out = new PrintWriter(bw)
			){
			
			out.print(text);
			
		} catch (IOException e) {
			System.out.println("ERROR in textToFile");
		}
		
	}
	
	public static String readFile(String fileName) throws RuntimeException{
		String text = "";
		String oneLine = "";
		
		try (FileReader fr = new FileReader(fileName);
			 BufferedReader in = new BufferedReader(fr)	) {
			
			while (true) {
				oneLine = in.readLine();
				
				if (oneLine == null)
					break;
				
				text = text + oneLine + "\n";
			}
			
		} catch (Exception e) {
			System.out.println("ERROR in readFile");
			
			throw new RuntimeException();
		}
		
		return text;
	}
	
	public static String usersToJson(List<User> users) {
		Gson gson = new Gson();
		
		return gson.toJson(users);
	}
	
	public static List<User> jsonToUsers(String jsonString){
		Type listType;
		Gson gson = new Gson();		
		
		List<User> list;
		
		listType = new TypeToken<LinkedList<User>>(){}.getType();
		list = gson.fromJson(jsonString, listType);
		
		return list;
	}
	
	public static User jsonToUser(String jsonString) {
		Gson gson = new Gson();	
		
		return gson.fromJson(jsonString, User.class);
	}
	
}
