package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import exceptions.OverDioException;
import models.Image;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ImageDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageDAO.class);
    private static final String DB_NAME = "overdio";
    private static final String DB_COLLECTION = "images";
    private final ObjectMapper mapper;

    private final MongoCollection<Document> collection;

    public ImageDAO(){
        this.mapper = new ObjectMapper();
        MongoClientURI uri = new MongoClientURI("mongodb://dio:mudamudamuda42@ds157064.mlab.com:57064/overdio");//todo
        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase(DB_NAME);
        this.collection = database.getCollection(DB_COLLECTION);
    }

    public List<Image> getImagesByTags(List<String> tags) throws IOException {
        FindIterable<Document> documents = collection.find(Filters.all("tagList",tags));
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
        image.setTagList(image.getTagList().stream().distinct().collect(Collectors.toList()));
        String jsonString = mapper.writeValueAsString(image);
        Document doc = Document.parse(jsonString);
        collection.insertOne(doc);
        //return "Utilisateur " + image.getFirstName() + " " + user.getLastName() + " added successfully.";
    }

    public void update(Image image) throws JsonProcessingException {
        String id = image.get_id();
        //Remove empty tags, only whitespaces tags, tags separated by whitespaces
        // and duplicated tags
        image.setTagList(image.getTagList().stream()
                .filter(tag -> !tag.isEmpty() && !tag.matches(" +"))
                .map(tag -> Arrays.asList(tag.split(" +")))
                .flatMap(Collection::stream)
                .filter(tag -> !tag.isEmpty())
                .distinct()
                .collect(Collectors.toList()));
        LOGGER.info("Updating image with id : {}", id);
        String jsonString = mapper.writeValueAsString(image);
        collection.replaceOne(Filters.eq("_id", id), Document.parse(jsonString));
    }

    public List<Image> getLastUploadedImages(int n) throws IOException {
        FindIterable<Document> documents = collection.find().sort(Sorts.descending("creationDate")).limit(n);
        List<Image> lastImages = new ArrayList<>();
        for(Document doc : documents){
            lastImages.add(mapper.readValue(doc.toJson(), Image.class));
        }
        return lastImages;
    }


}
