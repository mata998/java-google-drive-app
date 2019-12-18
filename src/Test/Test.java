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
		
		
//		String currentPath = "a/b/c/ddd/";
//		String selectedFile = "a/b/c/ddd/...";
		
		String currentPath = "Nesto/";
		String selectedFileName = "...";
		String selectedFilePath = currentPath + selectedFileName;
		
		System.out.println("Path: " + currentPath);
		System.out.println("Name: " + selectedFileName);
		System.out.println("FilePath: " + selectedFilePath);
		System.out.println();
		
		selectedFilePath = selectedFilePath.substring(0, selectedFilePath.lastIndexOf("/"));
		
		if (selectedFilePath.contains("/")) {
			selectedFilePath = selectedFilePath.substring(0, selectedFilePath.lastIndexOf("/"));
			currentPath = selectedFilePath.substring(0, selectedFilePath.lastIndexOf("/")+1);
			
			selectedFileName = selectedFilePath.substring(selectedFilePath.lastIndexOf("/")+1);
		}
		else { // GO BACK TO root dir
			selectedFilePath = null;
			currentPath = "";
			
			selectedFileName = null;
	
			
//			return;
		}
		
		System.out.println("Path: " + currentPath);
		System.out.println("Name: " + selectedFileName);
		System.out.println("FilePath: " + selectedFilePath);
		
		
	}
	
	public static void printUsers(List<User> list) {
		System.out.println("\nUsers:");
		
		for (User user : list) {
			System.out.println(" " + user.getUsername());
		}
		
		System.out.println();
	}

}
