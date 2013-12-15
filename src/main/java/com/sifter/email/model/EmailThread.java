package com.sifter.email.model;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class EmailThread {
	private String subject;
	private ArrayList<ThreadPart> threadParts = new  ArrayList<ThreadPart>();
	private Meta meta;
	public String getSubject(){
		return subject;
	}
	public void setSubject(String subject){
		this.subject = subject;
	}
	
	public ArrayList<ThreadPart> getThreadParts(){
		return threadParts;
	}
	public void setThreadParts(ArrayList<ThreadPart> threadParts){
		this.threadParts.clear();
		this.threadParts.addAll(threadParts);
	}
	
	public void addThreadPart(ThreadPart part){
		threadParts.add(part);
	}
	
	public void clearThreadParts(){
		threadParts.clear();
	}
	public Meta getMeta() {
		return meta;
	}
	public void setMeta(Meta meta) {
		this.meta = meta;
	}
}
