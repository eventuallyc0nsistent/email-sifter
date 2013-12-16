package com.sifter.email.model;

import java.util.ArrayList;
import java.util.HashSet;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class Summary {
	private Meta meta;
	private ArrayList<String> summary;
	
	public ArrayList<String> getSummary() {
		return summary;
	}
	public void setSummary(ArrayList<String> summary) {
		this.summary = summary;
	}
	public Meta getMeta() {
		return meta;
	}
	public void setMeta(Meta meta) {
		this.meta = meta;
	}

}
