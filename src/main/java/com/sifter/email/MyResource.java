package com.sifter.email;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import gate.*;
import gate.gui.*;
/**
 * Root resource (exposed at "myresource" path)
 */
@Path("resource")
public class MyResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }
    
    @Path("getname")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getName(@QueryParam("firstname") String firstName, @QueryParam("lastname") String lastName){
    	return "{name:" + firstName+ " " + lastName + "}";
    }
    
    @Path("startgate")
    @GET
    public void getGate() throws Exception{
    	Gate.init();
    	MainFrame.getInstance().setVisible(true);
    	Factory.newDocument("This is a new document");
    }
    
}
