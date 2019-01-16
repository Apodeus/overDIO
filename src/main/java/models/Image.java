package models;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Image {

    private String _id;
    private String imgUrl;
    private List<String> tagList;

    private String creationDate;

    public Image(){
        this(null);
    }

    public Image(String imgUrl){
        this(UUID.randomUUID().toString(), imgUrl, new ArrayList<>());
    }

    public Image(String _id, String imgUrl, List<String> tagList){
        this(_id, imgUrl, tagList, Timestamp.valueOf(LocalDateTime.now()));
    }

    public Image(String _id, String imgUrl, List<String> tagList, Timestamp timestamp){
        this._id = _id;
        this.imgUrl = imgUrl;
        this.tagList = tagList;
        this.creationDate = timestamp.toString();
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String toString(){
        return MessageFormat.format("id: {0}\nid_drive: {1}\ntags: {2}\ncreationDate: {3}\n",_id, imgUrl, tagList.toString(), creationDate.toString());
    }
}
