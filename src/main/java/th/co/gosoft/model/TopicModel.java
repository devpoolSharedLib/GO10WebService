package th.co.gosoft.model;

public class TopicModel {

    private String _id;
    private String _rev;
    private String topicId;
    private String user;
    private String subject;
    private String content;
    private String date;
    private String type;
    private String roomId;

    public TopicModel(){}
    
    public TopicModel(String _id, String date){
        this._id = _id;
        this.date = date;
    }
    
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
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
    public String getTopicId() {
        return topicId;
    }
    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getRoomId() {
        return roomId;
    }
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    
}
