package th.co.gosoft.go10.model;

import java.util.List;

public class QuestionModel {

    private String questionId;
    private String questionTitle;
    private List<ChoiceMasterModel> choiceMasterModelList;
    
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
    public List<ChoiceMasterModel> getChoiceMasterModelList() {
        return choiceMasterModelList;
    }
    public void setChoiceMasterModelList(List<ChoiceMasterModel> choiceMasterModelList) {
        this.choiceMasterModelList = choiceMasterModelList;
    }
    
}
