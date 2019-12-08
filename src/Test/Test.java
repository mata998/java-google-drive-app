package Test;

import java.util.LinkedList;
import java.util.List;

import server.FileConvertor;
import server.User;

public class Test {

	public static void main(String[] args) {
		
		List<User> lista = new LinkedList<>();
		
		String json = FileConvertor.readFile("src/server/users.json");
//		System.out.println(json);
		
		lista = FileConvertor.jsonToUsers(json);
		
		printUsers(lista);
		
		
//		lista.get(0).setFilesFromString("nesto.txt;nesto drugo.txt;trece nesto.txt;");
//		
//		json = FileConvertor.usersToJson(lista);
//		
//		FileConvertor.textToFile(json, "src/server/users.json");
		
	}
	
	public static void printUsers(List<User> list) {
		System.out.println("\nUsers:");
		
		for (User user : list) {
			System.out.println(" " + user.getUsername());
		}
		
		System.out.println();
	}

}
