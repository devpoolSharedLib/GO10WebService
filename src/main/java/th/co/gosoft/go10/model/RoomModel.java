package th.co.gosoft.go10.model;

import java.util.List;

public class RoomModel {

    private String _id;
    private String _rev;
    private String name;
    private String desc;
    private String type;
    private List<String> postUser;
    private List<String> commentUser;
    private List<String> readUser;
    
    public RoomModel(){}
    
    public RoomModel(String _id, String name){
        this._id = _id;
        this.name = name;
    }
    
    public String get_id() {
        return _id;
    }
    
    public void set_id(String _id) {
        this._id = _id;
    }
    
    public String get_rev() {
        return _rev;
    }
    
    public void set_rev(String _rev) {
        this._rev = _rev;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getPostUser() {
        return postUser;
    }

    public void setPostUser(List<String> postUser) {
        this.postUser = postUser;
    }

    public List<String> getCommentUser() {
        return commentUser;
    }

    public void setCommentUser(List<String> commentUser) {
        this.commentUser = commentUser;
    }

    public List<String> getReadUser() {
        return readUser;
    }

    public void setReadUser(List<String> readUser) {
        this.readUser = readUser;
    }

}
