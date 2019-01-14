package fr.overdio;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class MyApplication extends ResourceConfig {

    public MyApplication(){
        super(ImageManager.class, HelloWorldResource.class, MultiPartFeature.class);
    }

}
