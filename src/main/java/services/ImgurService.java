package services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ImgurService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImgurService.class);
    private static final String URL_IMGUR = "https://api.imgur.com/3/image";
    private static final int BUFFER_SIZE = 2048;
    private static final String DATA_NODE = "data";
    private static final String LINK_NODE = "link";
    private static final String CLIENT_ID = "Client-ID 1b90c8cef21c298";

    public String upload(String path) throws IOException {
        File img = new File(path);
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(URL_IMGUR);

        post.addHeader("Authorization", CLIENT_ID);

        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        entityBuilder.addPart("image", new FileBody(img));
        post.setEntity(entityBuilder.build());

        HttpResponse response = httpClient.execute(post);

        if(response.getStatusLine().getStatusCode() != 200){
            throw new ClientErrorException(Response.status(422, "Erreur durant l'upload, le fichier n'est pas une image ou un gif.").build());
        }

        byte[] buffer = new byte[BUFFER_SIZE];
        int lenght = response.getEntity().getContent().read(buffer);
        String imgurResponse = new String(Arrays.copyOf(buffer, lenght));

        JsonObject obj = new JsonParser().parse(imgurResponse).getAsJsonObject();
        String imgUrl = obj.get(DATA_NODE).getAsJsonObject().get(LINK_NODE).getAsString();
        LOGGER.info("URL on imgur is : {}", imgUrl);
        return imgUrl;
    }
}
