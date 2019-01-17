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

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
@Path("/images")
public class ImageManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageManager.class);

    private final ImageDAO imageDAO;
    private final ObjectMapper mapper;
    private final ImgurService imgurService;

    //Todo : Injecter ces services dans le constructeur plutot que de les
    // créer dedans => Permet de mieux tester la partie Back (métier)
    public ImageManager() {
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

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Image updateImage(Image image, @PathParam("id") String id) throws JsonProcessingException {
        if(!id.equals(image.get_id())){
            LOGGER.warn("L''id de l''image donnée ne correspond pas à l''id de la rout.\nAttendu : " + id + "\nRecu : " + image.get_id());
            throw new BadRequestException(Response.status(400, "L''id de l''image donnée ne correspond pas à l''id de la rout.\nAttendu : " + id + "\nRecu : " + image.get_id()).build());
        }
        Image imageDB;
        try {
            imageDB = imageDAO.getImageById(id);
        } catch (IOException e) {
            LOGGER.warn("Erreur lors de la récupération de l''image en BDD avec l''id = {}", id);
            throw new InternalServerErrorException(Response.status(500, "Erreur lors de la récupération de l''image en BDD avec l''id = " + id).build());
        }
        if(!imageDB.getImgUrl().equals(image.getImgUrl())){
            LOGGER.warn("L''image correspondant à la route et l''image donnée n''ont pas la meme url");
            throw new BadRequestException(Response.status(400, "L''image correspondant à la route et l''image donnée n''ont pas la meme url").build());
        }
        if(!imageDB.getCreationDate().equals(image.getCreationDate())){
            LOGGER.warn("L'image n'a pas la bonne date de creation !");
            throw new BadRequestException(Response.status(400, "L'image n'a pas la bonne date de creation !").build());
        }
        //Then update in DB
        LOGGER.info(image.getTagList().toString());
        try {
            imageDAO.update(image);
        } catch (JsonProcessingException e) {
            LOGGER.warn("Erreur lors de la mise à jour de l''image donnée en base de donnée");
            throw new BadRequestException(Response.status(400, "Erreur lors de la mise à jour de l''image donnée en base de donnée").build());
        }
        return image;
    }

    @GET
    @Path("/searchByTags")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Image> getImagesByTags(@QueryParam("tags") List<String> tags){
        LOGGER.info("Getting images using tags ...");
        List<Image> images;
        try {
            LOGGER.debug("requested tags : " + tags.toString() + " " + tags.size());
            images = imageDAO.getImagesByTags(tags);
        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedIOException(e);
        }
        if(images == null){
            throw new NotFoundException("Error no image found with these tags");
        }

        return images;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Image> getImagesByTags(){
        LOGGER.info("Getting last uploaded images ...");
        List<Image> images = null;
        try {
            images = imageDAO.getLastUploadedImages(10);
        } catch (IOException e) {
            throw new InternalServerErrorException(e.getMessage(), e);
        }
        return images.stream().sorted((img1, img2) -> Long.valueOf(img2.getCreationDate()).compareTo(Long.valueOf(img1.getCreationDate()))).collect(Collectors.toList());
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
            tagList = tagList.replaceAll(" +", " ");
            savedImage.setTagList(Arrays.asList(tagList.split(" +")));
            imageDAO.addImage(savedImage);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return mapper.writeValueAsString(savedImage);
    }
}
