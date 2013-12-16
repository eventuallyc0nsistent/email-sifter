package com.sifter.email.lib;

public enum NEEnum {
	DATE(4),		
	TIME(4), 		
	LOCATION(4), 	
	ORGANIZATION(3), 
	DURATION(3),
	PERSON(2), 	
	NUMBER(2), 
	MONEY(3); 	
	private int score;
	
	private NEEnum(int i){
		score = i;
	}
	public int score(){
		return score;
	}
}
