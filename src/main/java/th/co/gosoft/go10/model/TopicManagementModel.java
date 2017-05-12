package th.co.gosoft.go10.model;

import java.util.List;

public class TopicManagementModel {

    String bookmark;
    Integer totalRows;
    List<BoardContentModel> pinTopicList;
    List<BoardContentModel> noPinTopicList;
    List<LastTopicModel> unsavePinList;
    List<LastTopicModel> deletePinList;
    
    public String getBookmark() {
        return bookmark;
    }
    public void setBookmark(String bookmark) {
        this.bookmark = bookmark
        		;
    }
    public Integer getTotalRows() {
        return totalRows;
    }
    public void setTotalRows(Integer totalRows) {
        this.totalRows = totalRows;
    }
    public List<BoardContentModel> getPinTopicList() {
        return pinTopicList;
    }
    public void setPinTopicList(List<BoardContentModel> pinTopicList) {
        this.pinTopicList = pinTopicList;
    }
    public List<BoardContentModel> getNoPinTopicList() {
        return noPinTopicList;
    }
    public void setNoPinTopicList(List<BoardContentModel> noPinTopicList) {
        this.noPinTopicList = noPinTopicList;
    }
    public List<LastTopicModel> getUnsavePinList() {
        return unsavePinList;
    }
    public void setUnsavePinList(List<LastTopicModel> unsavePinList) {
        this.unsavePinList = unsavePinList;
    }
    public List<LastTopicModel> getDeletePinList() {
        return deletePinList;
    }
    public void setDeletePinList(List<LastTopicModel> deletePinList) {
        this.deletePinList = deletePinList;
    }
}
