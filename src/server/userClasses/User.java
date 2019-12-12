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
	
	
	// new stuff
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
	
	public void addSharedToUser(String user) {
		sharedTo.add(user);
	}
	
	public void addSharedFromUser(String user) {
		sharedFrom.add(user);
	}

	
	public String getFilesString() {
		String text = "";
		
		for (String file : files) {
			
			text = text + file + ";";
			
		}
		
		return text;
	}
	
	public String[] getFilesStringArr() {
		String[] arr = new String[files.size()];
		int n = 0;
		
		for (String x : files) {
			arr[n++] = x;
		}                         
		                 
		return arr;
	}
	
	public void setFilesFromString(String text) {
		
		String[] filesArr = text.split(";");
		
		files.clear();
		
		for (String file : filesArr) {
			files.add(file);
		}
		
	}
	
	public void addFile(String newFile) {
		files.add(newFile);
	}
	
	
	public void showAll() {
		System.out.println(username);
		System.out.println(password);
		System.out.println("Link: " + link);
		System.out.println("Link on: " + linkOn);
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
