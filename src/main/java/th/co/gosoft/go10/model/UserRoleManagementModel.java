package th.co.gosoft.go10.model;

public class UserRoleManagementModel {

    private String roomId;
    private String userPost;
    private String userComment;
    private String userRead;
    
    public String getRoomId() {
        return roomId;
    }
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    public String getUserPost() {
        return userPost;
    }
    public void setUserPost(String userPost) {
        this.userPost = userPost;
    }
    public String getUserComment() {
        return userComment;
    }
    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }
    public String getUserRead() {
        return userRead;
    }
    public void setUserRead(String userRead) {
        this.userRead = userRead;
    }
    
}
