package com.sifter.email.controller;
import gate.util.Out;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import com.sifter.email.lib.StanfordResources;
import com.sifter.email.model.*;
public class SummaryController {
	
	/**
	 * Builds index based on NER and spatial properties of the phrase
	 * @param list
	 * @param total
	 */
	private void buildIndex(ArrayList<Phrase> list, int total){
		StanfordResources sr = StanfordResources.getInstance();
		
		for(Phrase p:list){
			int score = sr.getNamedEntityScore(p.getPhrase()); 

			if(p.getPosition() <= total/2){
				score += (total/2) - p.getPosition();
			}
			else{
				score += p.getPosition() - (total/2);
			}
			p.setScore(score);
		}
	
	}
	/**
	 * Checks if the phrase has been repeated
	 * @param phrases
	 * @param phrase
	 * @return
	 */
	private boolean isRepeated(ArrayList<Phrase> phrases, Phrase phrase){
		for(Phrase p:phrases){
			if(p.getPhrase().contains(phrase.getPhrase()) || phrase.getPhrase().contains(p.getPhrase())){
				return true;
			}

			
		}
		return false;
	}
	
	/**
	 * Builds the summary model
	 * @param thread
	 * @param list
	 * @param total
	 * @return
	 */
	public Summary getSummary(EmailThread thread, ArrayList<Phrase> list, int total){
		buildIndex(list,total);
		ArrayList<Phrase> phrases = new ArrayList<Phrase>();
		HashSet<Integer> addedMessagePos = new HashSet<Integer>();
		Collections.sort(list,new PhraseScoreComparator());
		Summary summary = new Summary();

		int i = 0;
		
		while(phrases.size() <= 10 && i < list.size()){
			Phrase p = list.get(i++);
			if(!addedMessagePos.contains(p.getPosition()) || total <= 2){
				if(!isRepeated(phrases,p)){
					phrases.add(p);
					addedMessagePos.add(p.getPosition());
				}
			}
		}
		
		Collections.sort(phrases, new PhrasePositionComparator());
		ArrayList<String> summSet = new ArrayList<String>();
		summSet.add("Subject: "+thread.getSubject());
		for(Phrase p: phrases){
//			String sender = thread.getThreadParts().get(p.getPosition() - 1).getSenderName();
//			if(sender != null){
//				//sender = sender.split("[ ]*")[0];
//				summSet.add(sender+" says ..."+AnnotController.cleanString(p.getPhrase())+"...");
//			}
//			else
			String str = AnnotController.cleanString(p.getPhrase());
			if(!str.isEmpty())
				summSet.add("..."+str+"...");
			
		}
		summary.setSummary(summSet);
		summary.setMeta(thread.getMeta());
		return summary;
	}
	
	
	/**
	 * Compares in decreasing order of score
	 * @author svalmiki
	 *
	 */
	class  PhraseScoreComparator implements Comparator<Phrase>{
		@Override
		public int compare(Phrase p1, Phrase p2) {
			return p2.getScore() - p1.getScore() == 0? p2.getPhrase().length() - p1.getPhrase().length():p2.getScore() - p1.getScore();
		}
	}
	
	class  PhrasePositionComparator implements Comparator<Phrase>{
		@Override
		public int compare(Phrase p1, Phrase p2) {
			return p1.getPosition() - p2.getPosition();
		}
	}
	
	
	public static void main(String[] args) throws Exception{
		AnnotController aCtrl = new AnnotController();
		
		while(true){
			String path = "";
			
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println();
	        System.out.println("Enter 1 for practice set and 2 for test set and 0 to exit:");
	        try{
	            int i = Integer.parseInt(br.readLine());
	            if(i == 2){
	            	path = "testset/test/";
	            }
	            else if(i == 0){
	            	break;
	            }
	            
	            System.out.println("Enter name of document: ");
	            path = path+br.readLine();
	        }catch(NumberFormatException nfe){
	            System.err.println("Invalid Format!");
	        }
			
			
			
			EmailThread thread = aCtrl.buildThread(SummaryController.class.getResource("/docs/"+path));
			ArrayList<Phrase> list = new ArrayList<Phrase>();
			list = aCtrl.getPhrases();
			SummaryController sCtrl = new SummaryController();
			Summary summary = sCtrl.getSummary(thread, list, thread.getThreadParts().size()+1);
			
			System.out.println("All the people involved in the chain: \n");
			for(String s:summary.getMeta().getPeopleList()){
				System.out.println("\t"+s);
			}
			System.out.println();
			System.out.println("All the URLs mentioned in the chain: \n");
			for(String s:summary.getMeta().getUrlList()){
				System.out.println("\t"+s);
			}
			System.out.println();
			System.out.println("All the email addresses in the chain: \n");
			for(String s:summary.getMeta().getEmailList()){
				System.out.println("\t"+s);
			}
			System.out.println();
			System.out.println("All the times and dates mentioned in the chain: \n");
			for(String s:summary.getMeta().getDateTimeList()){
				System.out.println("\t"+s);
			}
			System.out.println();
			System.out.println();
			
			System.out.println("Summary phrases: \n");
			for(String s:summary.getSummary()){
				System.out.println("\t"+s);
			}
			System.out.println();
		}
		
		
		
	}
	
}
