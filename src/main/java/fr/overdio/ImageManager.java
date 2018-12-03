package fr.overdio;

import com.google.api.services.drive.model.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.DriveAuth;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;


@Path("/hello")
public class ImageManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageManager.class);

    private final DriveAuth drive;

    public ImageManager() throws GeneralSecurityException, IOException {
        this.drive = DriveAuth.getInstance();
    }


    @GET
    @Produces("text/plain")
    public String getImage() throws IOException {
        LOGGER.info("Requete GET : Recuperation des images");
        List<File> images = drive.getImages(Arrays.asList("1o3n_8ZVbmx2ZvB5ZKBHd4iBHYeCle5y9", "1UEup0JRETegBHBX2oE98Y9qdqXllHUfc"));
        if(images.isEmpty()){
            LOGGER.debug("Requete GET : Aucune image trouvee");
        }
        return toStringImage(images);
    }

    //Methode temporaire pour avoir une représentation sous forme de
    // string des informations sur les images recuperees sur le drive
    private String toStringImage(List<File> images) {
        StringBuilder res = new StringBuilder();
        for(File f : images){
            res.append(f.getName()).append(" | ").append(f.getId()).append("\n");
        }
        return res.toString();
    }
}
