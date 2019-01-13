package fr.overdio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.OverDioException;
import models.Image;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ImageDAO;
import services.ImgurService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.UUID;

@Path("/images")
public class ImageManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageManager.class);

    private final ImageDAO imageDAO;
    private final ObjectMapper mapper;
    private final ImgurService imgurService;

    //Todo : Injecter ces services dans le constructeur plutot que de les
    // créer dedans => Permet de mieux tester la partie Back (métier)
    public ImageManager() throws GeneralSecurityException, IOException {
        this.imgurService = new ImgurService();
        this.imageDAO = new ImageDAO();
        this.mapper = new ObjectMapper();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getImage(@PathParam("id") String id) throws IOException {
        LOGGER.info("Requete GET : Recuperation de l''image d''id : {}", id );
        Image image;
        try {
            image = imageDAO.getImageById(id);
        } catch (OverDioException e){
            //Write error code in response
            throw new NotFoundException("Can't find image with id : " + id);
        }
        return mapper.writeValueAsString(image);
    }

    @PUT //Should implement PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateImage(Image image, @PathParam("id") String id) throws JsonProcessingException {
        if(id != image.get_id()){
            LOGGER.warn("L''id de l''image donnée ne correspond pas à l''id de la route");
            throw new BadRequestException();
        }
        Image imageDB;
        try {
            imageDB = imageDAO.getImageById(id);
        } catch (IOException e) {
            LOGGER.warn("Erreur lors de la récupération de l''image en BDD avec l''id = {}", id);
            throw new InternalServerErrorException();
        }
        if(!imageDB.getImgUrl().equals(image.getImgUrl())){
            LOGGER.warn("L''image correspondant à la route et l''image donnée n''ont pas le meme id google drive");
            throw new BadRequestException();
        }
        //Then update in DB
        try {
            imageDAO.update(image);
        } catch (JsonProcessingException e) {
            LOGGER.warn("Erreur lors de la mise à jour de l''image donnée en base de donnée");
            throw new BadRequestException();
        }
        return mapper.writeValueAsString(image);
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String addImage(@FormDataParam("data") InputStream image,
                           @FormDataParam("tagList") String tagList) throws IOException {
        String fp = UUID.randomUUID().toString();
        String tmpFileLocation = "/tmp/" + fp + ".jpg";
        File f = new File(tmpFileLocation);

        FileOutputStream fos = new FileOutputStream(f);
        int read = 0;
        byte[] buffer = new byte[1024];
        while ((read = image.read(buffer)) != -1){
            fos.write(buffer, 0, read);
        }
        fos.flush();

        String imgUrl;
        Image savedImage;
        try {
            imgUrl = imgurService.upload(tmpFileLocation) ;
            savedImage = new Image(imgUrl);
            savedImage.setTagList(Arrays.asList(tagList.split(" ")));
            imageDAO.addImage(savedImage);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return mapper.writeValueAsString(savedImage);
    }
}
