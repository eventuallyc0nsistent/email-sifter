package com.sifter.email.model;

import java.util.ArrayList;

public class Meta {
	private ArrayList<String> emailList = null;
	private ArrayList<String> placeList = null;
	private ArrayList<String> dateTimeList = null;
	private ArrayList<String> moneyList = null;
	private ArrayList<String> urlList = null;
	private ArrayList<String> peopleList = null;
	
	public ArrayList<String> getPeopleList() {
		return peopleList;
	}
	public void setPeopleList(ArrayList<String> peopleList) {
		this.peopleList = peopleList;
	}
	public ArrayList<String> getEmailList() {
		return emailList;
	}
	public void setEmailList(ArrayList<String> emailList) {
		this.emailList = emailList;
	}
	public ArrayList<String> getPlaceList() {
		return placeList;
	}
	public void setPlaceList(ArrayList<String> placeList) {
		this.placeList = placeList;
	}
	public ArrayList<String> getDateTimeList() {
		return dateTimeList;
	}
	public void setDateTimeList(ArrayList<String> dateTimeList) {
		this.dateTimeList = dateTimeList;
	}
	public ArrayList<String> getMoneyList() {
		return moneyList;
	}
	public void setMoneyList(ArrayList<String> moneyList) {
		this.moneyList = moneyList;
	}
	public ArrayList<String> getUrlList() {
		return urlList;
	}
	public void setUrlList(ArrayList<String> urlList) {
		this.urlList = urlList;
	}
}
