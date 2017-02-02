package th.co.gosoft.go10.model;

public class RoomNotificationModel {

    private String _id;
    private String _rev;
    private String roomId;
    private String empEmail;
    private Integer countTopic;
    private String updateDate;
    
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
    public String getRoomId() {
        return roomId;
    }
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    public String getEmpEmail() {
        return empEmail;
    }
    public void setEmpEmail(String empEmail) {
        this.empEmail = empEmail;
    }
    public Integer getCountTopic() {
        return countTopic;
    }
    public void setCountTopic(Integer countTopic) {
        this.countTopic = countTopic;
    }
    public String getUpdateDate() {
        return updateDate;
    }
    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
