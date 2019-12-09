package server.userClasses;

import java.util.LinkedList;
import java.util.List;

public class UserPremium extends User{
	
	private List<Folder> listOfFolders = new LinkedList<>();
	
	public UserPremium() {
		Folder mainFolder = new Folder("main-folder");
		
		listOfFolders.add(mainFolder);
	}
	
}
