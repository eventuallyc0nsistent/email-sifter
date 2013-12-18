package com.sifter.email.model;

public class Scoring {
	public static int score_date = 10;
	public static int score_time = 10;
	public static int score_location = 10;
	public static int score_organization = 10;
	public static int score_duration = 10;
	public static int score_person = 10;
	public static int score_money = 10;


	public static final int INDEX_MAKER = 20;
	public static final int LEVEL_1_POS = 2;
	public static final int LEVEL_2_POS = 4;
	public static final int LEVEL_3_POS = 6;
	public static void clear(){
		score_date = 10;
		score_time = 10;
		score_location = 10;
		score_organization = 10;
		score_duration = 10;
		score_person = 10;
		score_money = 10;
	}
}
