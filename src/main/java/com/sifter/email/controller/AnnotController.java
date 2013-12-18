package com.sifter.email.controller;
import java.util.*;
import java.net.*;

import gate.*;
import gate.util.GateException;

import com.sifter.email.model.*;
import com.sifter.email.lib.*;
import com.sifter.email.lib.GateResources.SortedAnnotationList;

public class AnnotController {
	private EmailThread thread = new EmailThread();
	private GateResources gr;
	public AnnotController() throws Exception{
		gr = GateResources.getInstance();
		gr.initialize();
	}
	/**
	 * Builds Thread
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public EmailThread buildThread(URL url) throws Exception{
		 
		gr.buildCorpusWithDoc(url);
		gr.execute();
		
		
		
		//Get the subject
		HashSet<Annotation> subjAnnotSet = gr.getAnnotations(AnnotEnum.SubjectMail.name());
		if(subjAnnotSet.iterator().hasNext()){
			Annotation subjAnnot = subjAnnotSet.iterator().next();
			thread.setSubject(gr.getContentFromAnnot(subjAnnot));
		}
		
		GateResources.SortedAnnotationList sortedAnnots = new SortedAnnotationList();
		
		HashSet<Annotation> threadPartAnnotSet = gr.getAnnotations(AnnotEnum.ThreadPart.name());
		
		for(Annotation a: threadPartAnnotSet){
			sortedAnnots.addSortedExclusive(a);
		}
		
		//Build meta information
		Meta meta = buildMetaInformation();
		HashSet<String> peopleSet = new HashSet<String>();
		thread.clearThreadParts();
		ThreadPart tp = new ThreadPart();
		for (int i = 0; i < sortedAnnots.size(); ++i) {
			Annotation tpAnnot = (Annotation) sortedAnnots.get(i);
			if(gr.getContentFromCategory(tpAnnot,CategoryEnum.ThreadBody.getCategory()) != null){
				String body = gr.getContentFromCategory(tpAnnot,CategoryEnum.ThreadBody.getCategory());
				body = cleanString(body);
				if(body != null){
					tp.setBody(body);
				}
				
				thread.addThreadPart(tp);
				tp = new ThreadPart();
			}
			if(gr.getContentFromCategory(tpAnnot,CategoryEnum.FromEmail.getCategory()) != null){
				tp.setSenderEmail(gr.getContentFromCategory(tpAnnot,CategoryEnum.FromEmail.getCategory()));
			}
			if(gr.getContentFromCategory(tpAnnot,CategoryEnum.SentDate.getCategory()) != null){
				tp.setSentTime(gr.getContentFromCategory(tpAnnot,CategoryEnum.SentDate.getCategory()));
			}
			if(gr.getContentFromCategory(tpAnnot,CategoryEnum.SenderName.getCategory()) != null){
				String name = gr.getContentFromCategory(tpAnnot,CategoryEnum.SenderName.getCategory());
				//Add names of all active participants into meta information
				if(name != null && !name.trim().isEmpty()){
					tp.setSenderName(name.trim());
					peopleSet.add(name.trim());
				}
				else
				{
					tp.setSenderName("Someone");
				}
				
			}
		}
		meta.setPeopleList(peopleSet);
		thread.setMeta(meta);
		gr.freeResources();
		return thread;
	}
	
	/**
	 * Gets all the emails URLs, Names, etc.
	 * @return
	 * @throws Exception
	 */
	private Meta buildMetaInformation() throws Exception{
		Meta meta = new Meta();
		//Emails
		meta.setEmailList(buildAnnotsFromKind(AnnotEnum.Address.name(), KindEnum.email.name()));
		//URLs
		meta.setUrlList(buildAnnotsFromKind(AnnotEnum.Address.name(), KindEnum.url.name()));
		//DateTime
		meta.setDateTimeList(buildAnnots(AnnotEnum.Date.name()));
		//People
		//meta.setPeopleList(buildAnnots(AnnotEnum.Person.name()));
		//Address
		return meta;
	}
	
	/**
	 * Gets phrases as Phrase list
	 * @return
	 */
	
	public ArrayList<Phrase> getPhrases(){
		ArrayList<Phrase> phrases = new ArrayList<Phrase>();
		
		StanfordResources sr = StanfordResources.getInstance();
		int i = 0;
		
		ArrayList<String> strPhrases = new ArrayList<String>();
//		sr.buildPhrases(strPhrases,thread.getSubject());
//		buildPhraseList(phrases,strPhrases,i);
		
		for(ThreadPart tp: thread.getThreadParts()){
			strPhrases = new ArrayList<String>();
			sr.buildPhrases(strPhrases,tp.getBody());
			buildPhraseList(phrases,strPhrases,i);
			++i;
		}
		
		return phrases;
	}
	/**
	 * B
	 * @param phrases
	 * @param strPhrases
	 * @param pos
	 */
	public void buildPhraseList(ArrayList<Phrase> phrases, ArrayList<String> strPhrases, int pos){
		for(String s:strPhrases){
			Phrase p = new Phrase();
			p.setPhrase(s);
			p.setPosition(pos);
			p.setScore(0);
			phrases.add(p);
		}
	}
	/**
	 * gets all annotation string for an annotation
	 * @param annot
	 * @return
	 * @throws Exception
	 */
	private HashSet<String> buildAnnots(String annot) throws Exception{
		HashSet<Annotation> annots = gr.getAnnotations(annot);
		HashSet<String> list = new HashSet<String>();
		
		for(Annotation a : annots){
			String str = cleanString(gr.getContentFromAnnot(a));
			if(str != null){
				list.add(str);
			}
		}
		return list;
	}
	/**
	 * Builds annotations with category
	 * @param annot
	 * @param cat
	 * @return
	 * @throws Exception
	 */
	private HashSet<String> buildAnnotsFromCat(String annot, String cat) throws Exception{
		HashSet<Annotation> annots = gr.getAnnotations(annot);
		HashSet<String> list = new HashSet<String>();
		
		for(Annotation a : annots){
			String str = cleanString(gr.getContentFromCategory(a, cat));
			if(str != null){
				list.add(str);
			}
		}
		return list;
	}
	/**
	 * B
	 * @param annot
	 * @param kind
	 * @return
	 * @throws Exception
	 */
	private HashSet<String> buildAnnotsFromKind(String annot, String kind) throws Exception{
		HashSet<Annotation> annots = gr.getAnnotations(annot);
		HashSet<String> list = new HashSet<String>();
		
		for(Annotation a : annots){
			String str = cleanString(gr.getContentFromKind(a, kind));
			if(str != null){
				list.add(str);
			}
		}
		return list;
	}
	
	/**
	 * Cleans the string 
	 * @param str
	 * @return
	 */
	public static String cleanString(String str){
		if(str != null){
			str = str.replaceAll("Quoted.*\n?.*","");
			str = str.replaceAll("'d", " would");
			str = str.replaceAll("'m", " am");
			str = str.replaceAll("'ll", " will");
			str = str.replaceAll("Did'nt", "Did not");
			str = str.replaceAll("did'nt ", "did not");
			str = str.replaceAll("won't", "will not");
			str = str.replaceAll("Won't", "Will not");
			str = str.replaceAll("Had'nt", "Had not");
			str = str.replaceAll("had'nt", "had not");
			str = str.replaceAll("Can't", "Cannot");
			str = str.replaceAll("can't", "cannot");
			str = str.replaceAll("Should'nt", "Should not");
			str = str.replaceAll("should'nt", "should not");
			str = str.replaceAll("'ve", "have");
			str = str.replaceAll("'ve", "have");
			str = str.replaceAll("'re", "are");
			str = str.replaceAll("[Ss]ent[ ]+[Ff]rom[ ]+[Mm]y[ ]+.*", "");
			str = str.replaceAll("[Ss]ent[ ]+[Vv]ia[ ]+B.*", "");
			str = str.replaceAll("https://mail.google.com[/.?=&\\w\\d]*", "");
			str = str.replaceAll("[\\d]*[\\d]/[\\d]*[\\d]/[\\d]*[\\d][ ]*Gmail.*", "");
			//Tries to match signature
			str = str.replaceAll("\n((([Rr]egards)|([Th]anks)|([Bb]est)|([W]arm)).*[,.!]?).*([A-Z][a-zA-Z0-9,.-])*[ ]*([A-Z][a-zA-Z0-9,.-]*)*[ ]*.*\n?.*", "");
			//Matches a few unwanted dates
			str = str.replaceAll("[\\w]+[ ]+[\\d]+[ ]*,[ ]*[\\d]+[ ]*at[ ]*[\\d]+[ ]*:[ ]*[\\d]+[ ]*[\\w]*", "");
			//Matches dates like this: Wed, 19 Dec 2012 21:20:40+0800
			str = str.replaceAll("[A-Za-z]{3}[, ]+[0-9]+((th)|(rd))?[ ]*[,]?[ ]*[0-9]+[ ]*(at)?[ ]*.*", "");
			str = str.replaceAll("NOTICE.*", "");
			str = str.replaceAll("<.*>.*", "");
			str = str.replaceAll("\\[.*\\]", "");
			str = str.replaceAll("__+.*", "");
			str = str.replaceAll("-RSB-.*", "");
			str = str.replaceAll("-LSB-.*", "");
			str = str.replaceAll("-RRB-.*", "");
			str = str.replaceAll("-LRB-.*", "");
			str = str.replaceAll("Cc.*\n", "");
			str = str.replaceAll("``.*''", "");
			str = str.replaceAll("--\n.*", "");
			str = str.replaceAll("\n.*\\|.*", "");
			str = str.replaceAll("\n", " ");
			return str;
		}
		else
			return null;
		
	}
	
}