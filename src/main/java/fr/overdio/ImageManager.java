package fr.overdio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.OverDioException;
import models.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.DriveAuth;
import services.ImageDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.security.GeneralSecurityException;


@Path("/images")
public class ImageManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageManager.class);

    private final DriveAuth drive;
    private final ImageDAO imageDAO;
    private final ObjectMapper mapper;

    //Todo : Injecter ces services dans le constructeur plutot que de les
    // créer dedans => Permet de mieux tester la partie Back (métier)
    public ImageManager() throws GeneralSecurityException, IOException {
        this.drive = DriveAuth.getInstance();
        this.imageDAO = new ImageDAO();
        this.mapper = new ObjectMapper();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getImage(@PathParam("id") String id) throws IOException {
        LOGGER.info("Requete GET : Recuperation de l'image d'id : {}", id );
        Image image;
        try {
            image = imageDAO.getImageById(id);
        } catch (OverDioException e){
            //Write error code in response
            throw new NotFoundException();
        }
        return mapper.writeValueAsString(image);
    }

    // Not sure about the type of the paramater image
    // should maybe give only an array of tags ...

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateImage(Image image, @PathParam("id") String id) throws JsonProcessingException {
        if(id != image.get_id()){
            LOGGER.warn("L'id de l'image donnée ne correspond pas à l'id de la route");
            throw new BadRequestException();
        }
        Image imageDB;
        try {
            imageDB = imageDAO.getImageById(id);
        } catch (IOException e) {
            LOGGER.warn("Erreur lors de la récupération de l'image en BDD avec l'id = {}", id);
            throw new InternalServerErrorException();
        }
        if(!imageDB.getIdGoogleDrive().equals(image.getIdGoogleDrive())){
            LOGGER.warn("L'image correspondant à la route et l'image donnée n'ont pas le meme id google drive");
            throw new BadRequestException();
        }
        //Then update in DB
        try {
            imageDAO.update(image);
        } catch (JsonProcessingException e) {
            LOGGER.warn("Erreur lors de la mise à jour de l'image donnée en base de donnée");
            throw new BadRequestException();
        }
        return mapper.writeValueAsString(image);
    }


    // ID of images in Google Drive (TMP -> must be removed from code)
    //String dioID = "1o3n_8ZVbmx2ZvB5ZKBHd4iBHYeCle5y9";
    //String jojoID = "1UEup0JRETegBHBX2oE98Y9qdqXllHUfc";
}
