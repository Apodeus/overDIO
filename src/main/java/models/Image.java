package models;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Image {

    private String _id;
    private String idGoogleDrive;
    private List<String> tagList;

    public Image(){
        this(null);
    }

    public Image(String idGoogleDrive){
        this(UUID.randomUUID().toString(), idGoogleDrive, new ArrayList<>());
    }

    public Image(String _id, String idGoogleDrive, List<String> tagList){
        this._id = _id;
        this.idGoogleDrive = idGoogleDrive;
        this.tagList = tagList;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getIdGoogleDrive() {
        return idGoogleDrive;
    }

    public void setIdGoogleDrive(String idGoogleDrive) {
        this.idGoogleDrive = idGoogleDrive;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public String toString(){
        return MessageFormat.format("id: {0}\nid_drive: {1}\ntags: {2}",_id, idGoogleDrive, tagList.toString());
    }
}
