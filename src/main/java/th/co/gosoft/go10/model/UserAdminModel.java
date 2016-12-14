package th.co.gosoft.go10.model;

import java.util.List;

public class UserAdminModel {

    private String _id;
    private String _rev;
    private String empEmail;
    private String type;
    private List<String> roomAdmin;
    
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
    public String getEmpEmail() {
        return empEmail;
    }
    public void setEmpEmail(String empEmail) {
        this.empEmail = empEmail;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public List<String> getRoomAdmin() {
        return roomAdmin;
    }
    public void setRoomAdmin(List<String> roomAdmin) {
        this.roomAdmin = roomAdmin;
    }
}
