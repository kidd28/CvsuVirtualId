package com.example.cvsuvirtualid;

public class PostModel {

    String Caption, Date, FileId,FileName, Filelink, PostId;

    public PostModel() {}

    public PostModel(String Caption, String Date, String FileId,String FileName, String Filelink, String PostId){
        this.Caption = Caption;
        this.Date = Date;
        this.FileId = FileId;
        this.FileName = FileName;
        this.Filelink = Filelink;
        this.PostId = PostId;

    }

    public String getCaption() {
        return Caption;
    }

    public void setCaption(String caption) {
        Caption = caption;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getFileId() {
        return FileId;
    }

    public void setFileId(String fileId) {
        FileId = fileId;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFilelink() {
        return Filelink;
    }

    public void setFilelink(String filelink) {
        Filelink = filelink;
    }

    public String getPostId() {
        return PostId;
    }

    public void setPostId(String postId) {
        PostId = postId;
    }
}
