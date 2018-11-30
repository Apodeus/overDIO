package fr.overdio;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.security.GeneralSecurityException;

@Path("/hello")
public class HelloWorldResource {


    @GET
    @Produces("text/plain")
    public String getHelloWorld() throws IOException, GeneralSecurityException {
        likeMain();
        return "hello world !";
    }



    private void likeMain() throws IOException, GeneralSecurityException {
        /*

        FileList result = service.files().list()
                            .setPageSize(10)
                            .setFields("nextPageToken, files(id, name)")
                            .execute();
        List<File> files = result.getFiles();
        if(files == null || files.isEmpty()){
            System.out.println("No files found");
        } else {
            System.out.println("Files: ");
            files.forEach(f -> System.out.printf("%s (%s)\n", f.getName(), f.getId()));
        }
        */
    }
}