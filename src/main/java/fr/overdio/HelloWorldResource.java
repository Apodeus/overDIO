package fr.overdio;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.security.GeneralSecurityException;

@Path("/hi")
public class HelloWorldResource {

    @GET
    @Produces("text/plain")
    public String getHelloWorld() throws IOException, GeneralSecurityException {
        return "hello world !";
    }
}