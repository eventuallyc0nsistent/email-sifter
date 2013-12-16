package com.sifter.email.controller;
import gate.util.Out;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import com.sifter.email.lib.StanfordResources;
import com.sifter.email.model.*;
public class SummaryController {
	
	
	private void buildIndex(ArrayList<Phrase> list, int total){
		StanfordResources sr = StanfordResources.getInstance();
		
		for(Phrase p:list){
			int score = sr.getNamedEntityScore(p.getPhrase()); 
//			if(p.getPosition() == 0){
//				score += 5;
//			}
			if(p.getPosition() <= total/2){
				score += (total/2) - p.getPosition();
			}
			else{
				score += p.getPosition() - (total/2);
			}
			p.setScore(score);
		}
	
	}
	
	private boolean isRepeated(ArrayList<Phrase> phrases, Phrase phrase){
		for(Phrase p:phrases){
			if(p.getPhrase().contains(phrase.getPhrase()) || phrase.getPhrase().contains(p.getPhrase())){
				return true;
			}

			
		}
		return false;
	}
	
	
	public Summary getSummary(EmailThread thread, ArrayList<Phrase> list, int total){
		buildIndex(list,total);
		ArrayList<Phrase> phrases = new ArrayList<Phrase>();
		HashSet<Integer> addedMessagePos = new HashSet<Integer>();
		Collections.sort(list,new PhraseScoreComparator());
		Summary summary = new Summary();
//		for(int i = 0; i < 6; ++i){
//			if(i<list.size()){
//				phrases.add(list.get(i));
//			}
//		}
		int i = 0;
		while(phrases.size() <= 6 && i < list.size()){
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
		for(Phrase p: phrases){
			summSet.add(p.getPhrase());
		}
		summary.setSummary(summSet);
		summary.setMeta(thread.getMeta());
		return summary;
	}
	
	
	
	class  PhraseScoreComparator implements Comparator<Phrase>{
		@Override
		public int compare(Phrase p1, Phrase p2) {
			return p2.getScore() - p1.getScore() == 0? p1.getPhrase().length() - p2.getPhrase().length():p2.getScore() - p1.getScore();
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
		EmailThread thread = aCtrl.buildThread(SummaryController.class.getResource("/docs/testset/test/Cake.pdf"));
		ArrayList<Phrase> list = new ArrayList<Phrase>();
		list = aCtrl.getPhrases();
		SummaryController sCtrl = new SummaryController();
		sCtrl.getSummary(thread, list, thread.getThreadParts().size()+1);
		Out.prln(sCtrl.getSummary(thread, list, thread.getThreadParts().size()+1));
	}
	
}
