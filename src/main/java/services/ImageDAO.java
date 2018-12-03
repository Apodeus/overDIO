package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import exceptions.OverDioException;
import models.Image;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ImageDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageDAO.class);
    private static final String DB_NAME = "overDio";
    private static final String DB_COLLECTION = "images";
    private static ImageDAO imageDAO;
    private final ObjectMapper mapper;

    private final MongoDatabase database;
    private final MongoCollection<Document> collection;
    private final MongoClient mongoClient;

    private ImageDAO(){
        this.mapper = new ObjectMapper();
        this.mongoClient = new MongoClient();
        this.database = this.mongoClient.getDatabase(DB_NAME);
        this.collection = this.database.getCollection(DB_COLLECTION);
    }

    public static ImageDAO getInstance(){
        if(imageDAO == null) {
            imageDAO = new ImageDAO();
        }
        return imageDAO;
    }

    public List<Image> getImagesByTags(List<String> tagList) throws IOException {
        FindIterable<Document> documents = collection.find(Filters.all("tagList", tagList.toArray()));
        List<Image> taggedImages = new ArrayList<>();
        for (Document doc : documents) {
            taggedImages.add(mapper.readValue(doc.toJson(), Image.class));
        }
        return taggedImages;
    }

    public Image getImageById(String id) throws IOException {
        FindIterable<Document> documents = collection.find(Filters.eq("_id", id));
        Document doc = Optional.ofNullable(documents.first())
                .orElseThrow(() -> new OverDioException("No image found with id = " + id));
        return mapper.readValue(doc.toJson(), Image.class);
    }

    public void addImage(Image image) throws JsonProcessingException {
            String jsonString = mapper.writeValueAsString(image);
            Document doc = Document.parse(jsonString);
            collection.insertOne(doc);
            //return "Utilisateur " + image.getFirstName() + " " + user.getLastName() + " added successfully.";
    }

    public void update(Image image) throws JsonProcessingException {
        String id = image.get_id();
        LOGGER.info("Updating image with id : " + id);
        String jsonString = mapper.writeValueAsString(image);
        collection.replaceOne(Filters.eq("_id", id), Document.parse(jsonString));
    }


}
