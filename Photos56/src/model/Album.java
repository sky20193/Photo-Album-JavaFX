package model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Album class to hold information relevant to an album including
 * a list of photos and the earliest/latest date of any photos in the album.
 * 
 * @author Umang Patel
 * @author Akashkumar Patel
 */
public class Album implements Serializable, Comparable<Album> {
	private static final long serialVersionUID = 1L;
	String name;
	List<Photo> photos;
	Calendar startDate, endDate;
	
	public Album(String name){
		this.name = name;
		photos = new ArrayList<Photo>();
		recalculateDates();
	}
	
	public Album(String n, List<Photo> p) {
		name = n;
		photos = p;
		recalculateDates();
	}
	
	public Calendar getStartDate() {
		return startDate;
	}
	
	/**
	 * Converts Calendar date to String
	 * @return String representation of the start date.
	 */
	public String getStartDateString() {
		Date d = startDate.getTime();
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		return format.format(d);
	}
	
	public Calendar getEndDate() {
		return endDate;
	}
	
	/**
	 * Converts Calendar date to String
	 * @return String representation of the end date.
	 */
	public String getEndDateString() {
		Date d = endDate.getTime();
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		return format.format(d);
	}
	
	public List<Photo> getPhotos() {
		return photos;
	}
	
	public void addPhoto(Photo photo){
		photos.add(photo);
		this.recalculateDates();
	}
	
	public void removePhoto(Photo p) {
		photos.remove(p);
		this.recalculateDates();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getTotalPhotos() {
		return photos.size();
	}
	
	public String toString() {
		return name;
	}
	
	public int compareTo(Album a) {
		return name.compareTo(a.getName());
	}
	
	/**
	 * Finds the earliest/latest photo in the album.
	 */
	public void recalculateDates() {
		if(photos.isEmpty()) {
			startDate = Calendar.getInstance();
			endDate = Calendar.getInstance();
			return;
		}
		
		startDate = photos.get(0).getDateTime();
		endDate = photos.get(0).getDateTime();
		for(Photo p : photos) {
			if(p.getDateTime().compareTo(startDate) < 0)
				startDate = p.getDateTime();
			if(p.getDateTime().compareTo(endDate) > 0)
				endDate = p.getDateTime();
		}
	}
}