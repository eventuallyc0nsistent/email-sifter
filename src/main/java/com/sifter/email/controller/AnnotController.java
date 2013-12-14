package com.sifter.email.controller;
import java.util.*;
import java.net.*;

import gate.*;

import com.sifter.email.model.*;
import com.sifter.email.lib.*;

public class AnnotController {
	private EmailThread thread = new EmailThread();
	
	public EmailThread buildThread(URL url) throws Exception{
		GateResources gr = GateResources.getInstance();
		gr.initialize();
		gr.buildCorpusWithDoc(url);
		gr.execute();
		//Get the subject
		HashSet<Annotation> subjAnnotSet = gr.getAnnotations(AnnotEnum.SubjectMail.name());
		if(subjAnnotSet.iterator().hasNext()){
			Annotation subjAnnot = subjAnnotSet.iterator().next();
			thread.setSubject(gr.getContent(subjAnnot));
		}
		
		
		HashSet<Annotation> threadPartAnnotSet = gr.getAnnotations(AnnotEnum.ThreadPart.name());
		thread.clearThreadParts();
		ThreadPart tp = new ThreadPart();
		for(Annotation tpAnnot : threadPartAnnotSet){
			if(gr.getContent(tpAnnot,CategoryEnum.ThreadBody.getCategory()) != null){
				tp.setBody(gr.getContent(tpAnnot,CategoryEnum.ThreadBody.getCategory()));
				thread.addThreadPart(tp);
				tp = new ThreadPart();
			}
			if(gr.getContent(tpAnnot,CategoryEnum.FromEmail.getCategory()) != null){
				tp.setSenderEmail(gr.getContent(tpAnnot,CategoryEnum.FromEmail.getCategory()));
			}
			if(gr.getContent(tpAnnot,CategoryEnum.SentDate.getCategory()) != null){
				tp.setSentTime(gr.getContent(tpAnnot,CategoryEnum.SentDate.getCategory()));
			}
			if(gr.getContent(tpAnnot,CategoryEnum.SenderName.getCategory()) != null){
				tp.setSenderName(gr.getContent(tpAnnot,CategoryEnum.SenderName.getCategory()));
			}
		}
		return thread;
	}
	
}