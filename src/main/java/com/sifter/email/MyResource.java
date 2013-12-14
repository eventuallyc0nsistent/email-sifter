package com.sifter.email;

import java.io.File;
import java.net.MalformedURLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import gate.*;
import gate.gui.*;

import com.sifter.email.dao.*;
import com.sifter.email.model.*;
/**
 * Root resource (exposed at "resource" path)
 */
@Path("sifter")
public class MyResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     * @throws MalformedURLException 
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() throws MalformedURLException {
        //return "Got it!";
    	return new File(getClass().getResource("/docs/Gigzolo rehearsal.pdf").getPath()).toURI().toURL().toString();
    }
    
    @Path("getname")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getName(@QueryParam("firstname") String firstName, @QueryParam("lastname") String lastName){
    	return "{name:" + firstName+ " " + lastName + "}";
    }
    
    @Path("getthread")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public EmailThread getGate() throws Exception{
    	
    	ThreadDao tDao = new ThreadDao();
    	return tDao.getThreadForDoc(getClass().getResource("/docs/Gigzolo rehearsal.pdf"));
    	
    	//Gate.init();
    	//MainFrame.getInstance().setVisible(true);
    	//Factory.newDocument("This is a new document");
    	//return "Get Gate";
    }
    
}
