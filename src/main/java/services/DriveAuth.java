package services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import fr.overdio.HelloWorldResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DriveAuth {

    private final Drive service;

    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_METADATA_READONLY);
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final Logger LOGGER = LoggerFactory.getLogger(DriveAuth.class);

    private static DriveAuth instance;

    private DriveAuth() throws GeneralSecurityException, IOException {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        this.service = new Drive.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
                .setApplicationName("OverDIO")
                .build();
    }

    public static DriveAuth getInstance() throws GeneralSecurityException, IOException {
        if(instance == null){
            instance = new DriveAuth();
        }
        return instance;
    }

    private Credential getCredentials(NetHttpTransport httpTransport) throws IOException {
        //load
        InputStream in = HelloWorldResource.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        //Build flow and trigger user auth request
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public void saveImage(/*....*/){
        //todo : save ...
    }

    public void updateImage(/*...*/){
        //todo : update ...
    }

    public List<File> getImages(List<String> IDsImageList) throws IOException {
        LOGGER.info("Acces au stockage Drive pour recuperer les images");
        List<File> result = new ArrayList<>();
        for(String id : IDsImageList){
            result.add(service.files().get(id).execute());
        }
        return result;
    }

    //TODO: Pas sur que cette méthode soit utile, plus pour du debug
    public List<File> getImages() throws IOException {
        //todo : return list of images
        FileList result = service.files().list().execute();
        return result.getFiles();
    }


}
