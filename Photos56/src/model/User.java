package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class to hold all user information which consists of the username used
 * to log in to the application, as well as a list of albums owned by the user.
 * 
 * @author Umang Patel
 * @author Akashkumar Patel
 */
public class User implements Serializable, Comparable<User> {
	private static final long serialVersionUID = 1L;
	String name;
	ArrayList<Album> albums;
	
	public User() {
		name = "";
		albums = new ArrayList<Album>();
	}

	public User(String name) {
		this.name = name;
		albums = new ArrayList<Album>();
	}
	
	public void addAlbum(Album a) {
		albums.add(a);
	}
	
	public void removeAlbum(Album a) {
		albums.remove(a);
	}

	public ArrayList<Album> getAlbums(){
		return albums;
	}
	
	public String getName() {
		return name;
	}
	
	public int compareTo(User u) {
		return name.compareTo(u.getName());
	}
	
	public String toString() {
		return name + "\n";
	}	
}