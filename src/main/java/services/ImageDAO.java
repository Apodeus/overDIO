package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import models.Image;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageDAO {

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

    //todo: finir de définir les parametres ...
    public void addImage(Image image){

    }

    public void update(Image image){

    }


}
