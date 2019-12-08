package model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Class to hold Photo data including location(filename) of photo,
 * caption, tags, and date
 * 
 * @author Umang Patel
 * @author Akashkumar Patel
 */
public class Photo implements Serializable, Comparable<Photo> {

	private static final long serialVersionUID = 1L;
	String filename;
	String caption;
	List<Tag> tags;
	Calendar datetime;
	
	public Photo(String file) {
		filename = file;
		tags = new ArrayList<Tag>();
		caption = "No Caption Added Yet";
	}
	
	public String getCaption() {
		return caption;
	}
	
	public void setCaption(String c) {
		caption = c;
	}
	
	public void addTag(Tag t) {
		tags.add(t);
	}
	
	public void deleteTag(Tag t) {
		tags.remove(t);
	}
	
	public void setDateTime(Calendar c) {
		datetime = c;
	}
	
	public Calendar getDateTime() {
		return datetime;
	}
	
	/**
	 * Converts Calendar date to String
	 * @return String representation of the date.
	 */
	public String getDateTimeString() {
		Date d = datetime.getTime();
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		return format.format(d);
	}
	
	public List<Tag> getTags() {
		return tags;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public int compareTo(Photo p) {
		return datetime.compareTo(p.getDateTime());
	}
}