package com.sifter.email.model;

public class Phrase {
	private String phrase;
	private boolean containsDateTime;
	private boolean containsURL;
	private boolean containsAddress;
	private boolean containsPhone;
	private int index;
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getPhrase() {
		return phrase;
	}
	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}
	public boolean isContainsDateTime() {
		return containsDateTime;
	}
	public void setContainsDateTime(boolean containsDateTime) {
		this.containsDateTime = containsDateTime;
	}
	public boolean isContainsURL() {
		return containsURL;
	}
	public void setContainsURL(boolean containsURL) {
		this.containsURL = containsURL;
	}
	public boolean isContainsAddress() {
		return containsAddress;
	}
	public void setContainsAddress(boolean containsAddress) {
		this.containsAddress = containsAddress;
	}
	public boolean isContainsPhone() {
		return containsPhone;
	}
	public void setContainsPhone(boolean containsPhone) {
		this.containsPhone = containsPhone;
	}
	
	
}
