package th.co.gosoft.go10.model;

public class NewTopicModel {

    private String _id;
    private String _rev;
    private String topicId;
    private String empEmail;
    private String avatarName;
	private String avatarPic;
    private String subject;
    private String content;
    private String date;
    private String type;
    private String roomId;
    private int countLike;
    private String updateDate;

	public NewTopicModel(){}
    
    public NewTopicModel(String _id, String date, String content){
        this._id = _id;
        this.date = date;
        this.content = content;
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
    
    public String getAvatarName() {
		return avatarName;
	}

	public void setAvatarName(String avatarName) {
		this.avatarName = avatarName;
	}

	public String getAvatarPic() {
		return avatarPic;
	}

	public void setAvatarPic(String avatarPic) {
		this.avatarPic = avatarPic;
	}

    public String getEmpEmail() {
        return empEmail;
    }

    public void setEmpEmail(String empEmail) {
        this.empEmail = empEmail;
    }

    public int getCountLike() {
        return countLike;
    }

    public void setCountLike(int countLike) {
        this.countLike = countLike;
    }

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	
}