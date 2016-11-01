package th.co.gosoft.go10.model.v0;

public class TransactionModel {

    private String _id;
    private String _rev;
    private String topicId;
    private String empEmail;
	private String type;
	private String date;
   
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
    
}
