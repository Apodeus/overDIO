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
    private Timestamp creationDate;

    public Image(){
        this(null);
    }

    public Image(String imgUrl){
        this(UUID.randomUUID().toString(), imgUrl, new ArrayList<>());
    }

    public Image(String _id, String imgUrl, List<String> tagList){
        this._id = _id;
        this.imgUrl = imgUrl;
        this.tagList = tagList;
        this.creationDate = Timestamp.valueOf(LocalDateTime.now());
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

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public String toString(){
        return MessageFormat.format("id: {0}\nid_drive: {1}\ntags: {2}\ncreationDate: {3}\n",_id, imgUrl, tagList.toString(), creationDate.toString());
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }
}
