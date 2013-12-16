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
		return aCtrl.buildThread(u);
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
		EmailThread thread = aCtrl.buildThread(u);
		ArrayList<Phrase> phrases = aCtrl.getPhrases();
		return sCtrl.getSummary(thread, phrases, thread.getThreadParts().size()+1);
		
	}
}
