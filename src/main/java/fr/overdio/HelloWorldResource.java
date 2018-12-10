package fr.overdio;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.lang.reflect.Array;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

@Path("/hi")
public class HelloWorldResource {

    @GET
    @Produces("text/plain")
    public String getHelloWorld() throws IOException, GeneralSecurityException {
        return "hello world !";
    }
}