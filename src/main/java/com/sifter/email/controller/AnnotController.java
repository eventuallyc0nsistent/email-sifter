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
		thread.setMeta(buildMetaInformation());
		
		thread.clearThreadParts();
		ThreadPart tp = new ThreadPart();
		for (int i = 0; i < sortedAnnots.size(); ++i) {
			Annotation tpAnnot = (Annotation) sortedAnnots.get(i);
			if(gr.getContentFromCategory(tpAnnot,CategoryEnum.ThreadBody.getCategory()) != null){
				String body = gr.getContentFromCategory(tpAnnot,CategoryEnum.ThreadBody.getCategory());
				body.replaceAll("\\[Quoted  text  hidden\\]", "");
				body.replaceAll("\\n", " ");
				tp.setBody(body);
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
				tp.setSenderName(gr.getContentFromCategory(tpAnnot,CategoryEnum.SenderName.getCategory()));
			}
		}
		return thread;
	}
	
	
	private Meta buildMetaInformation() throws Exception{
		Meta meta = new Meta();
		//Emails
		meta.setEmailList(buildAnnotsFromKind(AnnotEnum.Address.name(), KindEnum.email.name()));
		//URLs
		meta.setUrlList(buildAnnotsFromKind(AnnotEnum.Address.name(), KindEnum.url.name()));
		//DateTime
		meta.setDateTimeList(buildAnnots(AnnotEnum.Date.name()));
		//People
		meta.setPeopleList(buildAnnots(AnnotEnum.Person.name()));
		//Address
		return meta;
	}
	
	
	private ArrayList<String> buildAnnots(String annot) throws Exception{
		HashSet<Annotation> annots = gr.getAnnotations(annot);
		ArrayList<String> list = new ArrayList<String>();
		
		for(Annotation a : annots){
			String str = gr.getContentFromAnnot(a);
			if(str != null){
				list.add(str);
			}
		}
		return list;
	}
	
	private ArrayList<String> buildAnnotsFromCat(String annot, String cat) throws Exception{
		HashSet<Annotation> annots = gr.getAnnotations(annot);
		ArrayList<String> list = new ArrayList<String>();
		
		for(Annotation a : annots){
			String str = gr.getContentFromCategory(a, cat);
			if(str != null){
				list.add(str);
			}
		}
		return list;
	}
	
	private ArrayList<String> buildAnnotsFromKind(String annot, String kind) throws Exception{
		HashSet<Annotation> annots = gr.getAnnotations(annot);
		ArrayList<String> list = new ArrayList<String>();
		
		for(Annotation a : annots){
			String str = gr.getContentFromKind(a, kind);
			if(str != null){
				list.add(str);
			}
		}
		return list;
	}
}