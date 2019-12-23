package server.userClasses;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class User implements Serializable{
	private String username;
	private String password;
	private List<String> files = new LinkedList<>();
	private String link = "";
	private boolean linkOn = false;
	private boolean isPremium = false;
	private List<String> sharedTo = new LinkedList<>();
	private List<String> sharedFrom = new LinkedList<>();
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<String> getFiles() {
		return files;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public boolean isLinkOn() {
		return linkOn;
	}
	public void setLinkOn(boolean linkOn) {
		this.linkOn = linkOn;
	}
	public List<String> getSharedTo() {
		return sharedTo;
	}
	public List<String> getSharedFrom() {
		return sharedFrom;
	}
	public boolean isPremium() {
		return isPremium;
	}
	public void setPremium(boolean isPremium) {
		this.isPremium = isPremium;
	}
	
	
	public void addFile(String newFile) {
		files.add(newFile);
	}
	
	public void removeFile(String file) {
		
		files.remove(file);
		
	}
	
	public void renameFile(String oldName, String newName) {
		
		for (int i = 0; i<files.size(); i++) {
			
			if (files.get(i).equals(oldName)) {
				
				files.set(i, newName);
				
			}
			
		}
		
	}
	
	public void addSharedToUser(String user) {
		sharedTo.add(user);
	}
	
	public void addSharedFromUser(String user) {
		sharedFrom.add(user);
	}


	public boolean fileExists(String file) {
		for (String x : files) {
			if (x.equals(file)) {
				return true;
			}
			                   
		}
		
		return false;
	}

	public boolean sharedToUserExists(String user) {
		for (String x : sharedTo) {
			if (x.equals(user)) {
				return true;
			}
			                   
		}
		
		return false;
	}

	public boolean sharedFromUserExists(String user) {
		for (String x : sharedFrom) {
			if (x.equals(user)) {
				return true;
			}
			                   
		}
		
		return false;
	}
	
	
	public String[] getFilesStringArr() {
		String[] arr = new String[files.size()];
		int n = 0;
		
		for (String x : files) {
			arr[n++] = x;
		}                         
		                 
		return arr;
	}
	
	public String getFilesSemiColon() {
		String text = "";
		
		for (String x : files) {
			text = text + x + ";";
		}
		
		return text;
	}
	
	public String[] getSharedToUsersStringArr() {
		String[] arr = new String[sharedTo.size()];
		int n = 0;
		
		for (String x : sharedTo) {
			arr[n++] = x;
		}                         
		                 
		return arr;
	}

	public String[] getSharedFromUsersStringArr() {
		String[] arr = new String[sharedFrom.size()];
		int n = 0;
		
		for (String x : sharedFrom) {
			arr[n++] = x;
		}                         
		                 
		return arr;
	}

	
	
	
	public void showAll() {
		System.out.println(username);
		System.out.println(password);
		System.out.println("Link: " + link);
		System.out.println("Link on: " + linkOn);
		System.out.println("Premium: " + isPremium);
		System.out.println("Files: ");
		for (String x : files) {
			System.out.println(" " + x);
		}
		System.out.println("Shared to: ");
		for (String x : sharedTo) {
			System.out.println(" " + x);
		}
		System.out.println("Shared from: ");
		for (String x : sharedFrom) {
			System.out.println(" " + x);
		}
		
		
	}

}
