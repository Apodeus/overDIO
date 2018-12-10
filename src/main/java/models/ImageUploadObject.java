package models;

import java.util.List;

public class ImageUploadObject {
    private byte[] data;
    private List<String> tagList;

    public ImageUploadObject(byte[] data, List<String> tagList) {
        this.data = data;
        this.tagList = tagList;
    }

    public ImageUploadObject() {
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }
}
