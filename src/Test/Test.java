package Test;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

import server.FileConvertor;
import server.userClasses.User;

public class Test {

	public static void main(String[] args) {
		
		List<User> lista = new LinkedList<>();
		
		// read
//		String json = FileConvertor.readFile("src/server/users.json");
		
//		lista = FileConvertor.jsonToUsers(json);
		
//		printUsers(lista);
		
		
		
		//// write
//		json = FileConvertor.usersToJson(lista);
//		
//		FileConvertor.textToFile(json, "src/server/users.json");
		
		
		String oldPath = "src/server/database/z/nesto.txt";
		String targetFolder = "src/server/database/za/";
		
		File file = new File(oldPath);
		
		
		String newPath = targetFolder + file.getName();
		
				
		File movedFIle = new File(newPath);
		
		file.renameTo(movedFIle);
		
		
	}
	
	public static void printUsers(List<User> list) {
		System.out.println("\nUsers:");
		
		for (User user : list) {
			System.out.println(" " + user.getUsername());
		}
		
		System.out.println();
	}

}
