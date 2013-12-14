package com.sifter.email.dao;

import java.io.File;
import java.net.URL;

import com.sifter.email.model.*;
import com.sifter.email.controller.*;
public class ThreadDao {
	public EmailThread getThreadForDoc(URL u) throws Exception{
		AnnotController aCtrl = new AnnotController();
		return aCtrl.buildThread(u);
	}
}
