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
		HashMap<Integer,Integer> addedMessagePos = new HashMap<Integer,Integer>();
		Collections.sort(list,new PhraseScoreComparator());
		Summary summary = new Summary();

		int i = 0;

		while(phrases.size() <= 10 && i < list.size()){
			Phrase p = list.get(i++);
			if(!addedMessagePos.containsKey(p.getPosition()) || (addedMessagePos.containsKey(p.getPosition()) && (addedMessagePos.get(p.getPosition()) <= 2)) || total <= 3){
				p.setPhrase(AnnotController.cleanString(p.getPhrase().trim()));
				if(!isRepeated(phrases,p) && p.getPhrase().split("[^A-Za-z0-9]+").length > 2){
					phrases.add(p);
					if(addedMessagePos.containsKey(p.getPosition())){
						int val = addedMessagePos.get(p.getPosition());
						addedMessagePos.put(p.getPosition(),val+1);
					}
					else{
						addedMessagePos.put(p.getPosition(),1);
					}
				}
			}
		}

		Collections.sort(phrases, new PhrasePositionComparator());
		ArrayList<String> summSet = new ArrayList<String>();
		for(Phrase p: phrases){
			String str = AnnotController.cleanString(p.getPhrase());
			String sender = thread.getThreadParts().get(p.getPosition()).getSenderName();
			if(sender != null && !sender.trim().isEmpty()){
				if(!str.isEmpty()){
					sender = sender.split(" ")[0];
					summSet.add(sender+" says \""+str.trim()+"\"");
				}
			}
			else{
				if(!str.isEmpty())
					summSet.add("Someone says \""+str.trim()+"\"");
			}


		}
		summary.setSubject(thread.getSubject());
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
		EmailThread thread = new EmailThread();
		while(true){
			String path = "";

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println();
			System.out.println("Enter 1 for practice set and 2 for test set and 0 to exit:");
			try{
				boolean isError = false;
				try{
					int i = Integer.parseInt(br.readLine());
					if(i == 2){
						path = "testset/test/";
					}
					else if(i == 0){
						break;
					}
				}
				catch(NumberFormatException nfe){
					System.err.println("Please enter only 0, 1 or 2");
					isError = true;
				}
				if(!isError){
					System.out.println("Enter name of document: ");
					path = path+br.readLine();
					
					
					thread = aCtrl.buildThread(SummaryController.class.getResource("/docs/"+path));
					ArrayList<Phrase> list = new ArrayList<Phrase>();
					list = aCtrl.getPhrases();
					SummaryController sCtrl = new SummaryController();
					Summary summary = sCtrl.getSummary(thread, list, thread.getThreadParts().size()+1);


					System.out.println("All the people actively involved in the chain (all who wrote mails): \n");
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

					System.out.println("Subject: \n");
					System.out.println("\t*\t"+summary.getSubject());
					System.out.println();

					System.out.println("Summary phrases: \n");
					for(String s:summary.getSummary()){
						System.out.println("\t"+s);
					}
					System.out.println();
				}
				
				
				
			}catch(Exception nfe){
				nfe.printStackTrace();
				System.err.println("Issue with parsing the document. Please check if the document exists before running this.");
			}



//			thread = aCtrl.buildThread(SummaryController.class.getResource("/docs/"+path));
//			ArrayList<Phrase> list = new ArrayList<Phrase>();
//			list = aCtrl.getPhrases();
//			SummaryController sCtrl = new SummaryController();
//			Summary summary = sCtrl.getSummary(thread, list, thread.getThreadParts().size()+1);
//
//
//			System.out.println("All the people actively involved in the chain (all who wrote mails): \n");
//			for(String s:summary.getMeta().getPeopleList()){
//				System.out.println("\t"+s);
//			}
//			System.out.println();
//			System.out.println("All the URLs mentioned in the chain: \n");
//			for(String s:summary.getMeta().getUrlList()){
//				System.out.println("\t"+s);
//			}
//			System.out.println();
//			System.out.println("All the email addresses in the chain: \n");
//			for(String s:summary.getMeta().getEmailList()){
//				System.out.println("\t"+s);
//			}
//			System.out.println();
//			System.out.println("All the times and dates mentioned in the chain: \n");
//			for(String s:summary.getMeta().getDateTimeList()){
//				System.out.println("\t"+s);
//			}
//			System.out.println();
//			System.out.println();
//
//			System.out.println("Subject: \n");
//			System.out.println("\t*\t"+summary.getSubject());
//			System.out.println();
//
//			System.out.println("Summary phrases: \n");
//			for(String s:summary.getSummary()){
//				System.out.println("\t"+s);
//			}
//			System.out.println();
		}



	}

}
