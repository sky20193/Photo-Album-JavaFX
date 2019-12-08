package model;

import java.io.Serializable;

/**
 * Class to represent a tag (key/value pair) used to categorize photos.
 * 
 * @author Umang Patel
 * @author Akashkumar Patel
 */
public class Tag implements Comparable<Tag>, Serializable{
	private static final long serialVersionUID = 1L;
	private String mType;
	private String mValue;

	public Tag(String type, String value){
		this.setType(type);
		this.setValue(value);
	}

	public String getType() {
		return mType;
	}

	public void setType(String type) {
		mType = type;
	}

	public String getValue() {
		return mValue;
	}
	
	public void setValue(String value) {
		mValue = value;
	}
	
	public String toString(){
		return this.getType() + ": " + this.getValue();
	}
	
	public boolean equals(Object object){
		if (object == null || !(object instanceof Tag)) {
			return false;
		}
		
		Tag tag = (Tag) object;
		
		if (tag.getType().equals("")){
			return this.getValue().toLowerCase().equals(tag.getValue().toLowerCase());
		}
		if (tag.getValue().equals("")){
			return this.getType().toLowerCase().equals(tag.getType().toLowerCase());
		}
		return (this.getType().toLowerCase().equals(tag.getType().toLowerCase()) 
				&& this.getValue().toLowerCase().equals(tag.getValue().toLowerCase()));
	}

	@Override
	public int compareTo(Tag other) {
		if (this.getType().toLowerCase().equals(other.getType().toLowerCase())){
			return this.getValue().toLowerCase().compareTo(other.getValue().toLowerCase());
		}else{
			return this.getType().toLowerCase().compareTo(other.getType().toLowerCase());
		}
	}	
}