package server.userClasses;

import java.util.LinkedList;
import java.util.List;

public class Folder {
	private String name;
	private List<String> files = new LinkedList<>();
	
	public Folder(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getFiles() {
		return files;
	}
	public void setFiles(List<String> files) {
		this.files = files;
	}
	
	
	
}
