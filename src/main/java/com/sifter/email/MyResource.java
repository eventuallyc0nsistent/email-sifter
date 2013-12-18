package com.sifter.email;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;

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
        return "Got it!";
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
    public EmailThread getThread() throws Exception{
    	ThreadDao tDao = new ThreadDao();
    	return tDao.getThreadForDoc(getClass().getResource("/docs/Gigzolo rehearsal.pdf"));
    }
    @Path("getsample")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Summary getSample() throws Exception{
    	ThreadDao tDao = new ThreadDao();
    	return tDao.getSummaryForDoc(getClass().getResource("/docs/Gigzolo rehearsal.pdf"));
    }
    
    
    @Path("getsummary/{path}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Summary getSummary(@PathParam("path") String path) throws Exception{
    	ThreadDao tDao = new ThreadDao();
    	return tDao.getSummaryForDoc(getClass().getResource("/docs/testset/test/"+path));
    }
    
    @Path("getallsummary")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Summary> getAllSummary() throws Exception{
    	ArrayList<Summary> summaries = new ArrayList<Summary>();
    	File dir = new File(getClass().getResource("/docs/testset/test/").toURI());
    	ThreadDao tDao = new ThreadDao();
    	for(File f:dir.listFiles()){
    		summaries.add(tDao.getSummaryForDoc(f.toURI().toURL()));
    	}
    	
    	return summaries;
    }
    
}
