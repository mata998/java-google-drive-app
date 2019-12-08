package server;

import java.util.LinkedList;
import java.util.List;

public class User {
	private String username;
	private String password;
	private List<String> files = new LinkedList<>();
	
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
	
	public String getFilesString() {
		String text = "";
		
		for (String file : files) {
			
			text = text + file + ";";
			
		}
		
		return text;
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
	
}
