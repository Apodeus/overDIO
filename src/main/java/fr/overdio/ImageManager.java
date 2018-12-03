package fr.overdio;

import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.OverDioException;
import models.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.DriveAuth;
import services.ImageDAO;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.security.GeneralSecurityException;


@Path("/images")
public class ImageManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageManager.class);

    private final DriveAuth drive;
    private final ImageDAO imageDAO;
    private final ObjectMapper mapper;

    public ImageManager() throws GeneralSecurityException, IOException {
        this.drive = DriveAuth.getInstance();
        this.imageDAO = ImageDAO.getInstance();
        this.mapper = new ObjectMapper();
    }

    // ID of images in Google Drive (TMP -> must be removed from code)
    //String dioID = "1o3n_8ZVbmx2ZvB5ZKBHd4iBHYeCle5y9";
    //String jojoID = "1UEup0JRETegBHBX2oE98Y9qdqXllHUfc";

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getImage(@PathParam("id") String id) throws IOException {
        LOGGER.info("Requete GET : Recuperation de l'image d'id : " + id);
        Image image = null;
        try {
            image = imageDAO.getImageById(id);
        } catch (OverDioException e){
            //Write error code in response
            throw e;
        }
        return mapper.writeValueAsString(image);
    }
}
