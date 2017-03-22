package th.co.gosoft.go10.model;

import java.util.List;

public class BoardContentModel {

    private List<LastTopicModel> boardContentList;
    private PollModel pollModel;
    private Integer countAcceptPoll;
    
    public PollModel getPollModel() {
        return pollModel;
    }
    public void setPollModel(PollModel pollModel) {
        this.pollModel = pollModel;
    }
    public Integer getCountAcceptPoll() {
        return countAcceptPoll;
    }
    public void setCountAcceptPoll(Integer countAcceptPoll) {
        this.countAcceptPoll = countAcceptPoll;
    }
    public List<LastTopicModel> getBoardContentList() {
        return boardContentList;
    }
    public void setBoardContentList(List<LastTopicModel> boardContentList) {
        this.boardContentList = boardContentList;
    }
}
