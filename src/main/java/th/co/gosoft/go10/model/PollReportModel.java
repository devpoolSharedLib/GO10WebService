package th.co.gosoft.go10.model;

import java.util.List;

public class PollReportModel {

	private Integer countAcceptPoll;
    private List<QuestionReportModel> questionReport;
    private List<String> empEmailAcceptPoll;
    
	public List<QuestionReportModel> getQuestionReport() {
		return questionReport;
	}
	public void setQuestionReport(List<QuestionReportModel> questionReport) {
		this.questionReport = questionReport;
	}
	public Integer getCountAcceptPoll() {
		return countAcceptPoll;
	}
	public void setCountAcceptPoll(Integer countAcceptPoll) {
		this.countAcceptPoll = countAcceptPoll;
	}
	public List<String> getEmpEmailAcceptPoll() {
		return empEmailAcceptPoll;
	}
	public void setEmpEmailAcceptPoll(List<String> empEmailAcceptPoll) {
		this.empEmailAcceptPoll = empEmailAcceptPoll;
	}	
	
}
