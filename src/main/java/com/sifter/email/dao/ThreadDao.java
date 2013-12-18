package com.sifter.email.dao;

import java.net.URL;
import java.util.ArrayList;

import com.sifter.email.model.*;
import com.sifter.email.controller.*;
public class ThreadDao {
	/**
	 * Gets email thread for the service
	 * @param u
	 * @return
	 * @throws Exception
	 */
	public EmailThread getThreadForDoc(URL u) throws Exception{
		AnnotController aCtrl = new AnnotController();
		EmailThread thread = new EmailThread();
		ArrayList<Phrase> list = new ArrayList<Phrase>();
		aCtrl.buildThreadAndPhraseList(u,thread,list);
		return thread;
	}
	/**
	 * Gets summary for the service
	 * @param u
	 * @return
	 * @throws Exception
	 */
	public Summary getSummaryForDoc(URL u) throws Exception{
		AnnotController aCtrl = new AnnotController();
		SummaryController sCtrl = new SummaryController();
		EmailThread thread = new EmailThread();
		ArrayList<Phrase> list = new ArrayList<Phrase>();
		aCtrl.buildThreadAndPhraseList(u,thread,list);
		Summary summary = sCtrl.getSummary(thread, list, thread.getThreadParts().size());
		return summary;
		
	}
}
