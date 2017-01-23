package th.co.gosoft.go10.model;

import java.util.List;

public class UserRoleManagementModel {

    private String roomId;
    private List<String> postUser;
    private List<String> commentUser;
    private List<String> readUser;
    private List<UserModel> postUserModelList;
    private List<UserModel> commentUserModelList;
    private List<UserModel> readUserModelList;
    
    public String getRoomId() {
        return roomId;
    }
    public void setRoomId(String roomId) {
        this.roomId = roomId;
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
    public List<UserModel> getPostUserModelList() {
        return postUserModelList;
    }
    public void setPostUserModelList(List<UserModel> postUserModelList) {
        this.postUserModelList = postUserModelList;
    }
    public List<UserModel> getCommentUserModelList() {
        return commentUserModelList;
    }
    public void setCommentUserModelList(List<UserModel> commentUserModelList) {
        this.commentUserModelList = commentUserModelList;
    }
    public List<UserModel> getReadUserModelList() {
        return readUserModelList;
    }
    public void setReadUserModelList(List<UserModel> readUserModelList) {
        this.readUserModelList = readUserModelList;
    }
}
