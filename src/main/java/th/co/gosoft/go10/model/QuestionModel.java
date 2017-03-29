package th.co.gosoft.go10.model;

import java.util.List;

public class QuestionModel {

    private String questionId;
    private String questionTitle;
    private List<ChoiceMasterModel> choiceMaster;
    
    public String getQuestionId() {
        return questionId;
    }
    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
    public String getQuestionTitle() {
        return questionTitle;
    }
    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }
    public List<ChoiceMasterModel> getChoiceMaster() {
        return choiceMaster;
    }
    public void setChoiceMaster(List<ChoiceMasterModel> choiceMaster) {
        this.choiceMaster = choiceMaster;
    }
    
}
