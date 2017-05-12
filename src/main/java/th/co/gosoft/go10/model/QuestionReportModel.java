package th.co.gosoft.go10.model;

import java.util.List;

public class QuestionReportModel {

    private String questionId;
    private String questionTitle;
    private List<ChoiceReportModel> choiceReport;
    	
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
    public List<ChoiceReportModel> getChoiceMaster() {
        return choiceReport;
    }
    public void setChoiceReportModel(List<ChoiceReportModel> choiceReport) {
        this.choiceReport = choiceReport;
    }
    
}
