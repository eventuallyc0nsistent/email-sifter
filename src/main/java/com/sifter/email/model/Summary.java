package com.sifter.email.model;

import java.util.HashSet;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class Summary {
	private Meta meta;
	private HashSet<String> summary;
	
	public Meta getMeta() {
		return meta;
	}
	public void setMeta(Meta meta) {
		this.meta = meta;
	}
	public HashSet<String> getSummary() {
		return summary;
	}
	public void setSummary(HashSet<String> summary) {
		this.summary = summary;
	}
	
	
}
