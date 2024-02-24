package Bamboo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Application {

    private Integer id;
    private String appliedDate;
    private Status status;
    private Integer rating;
    private Applicant applicant;
    private Job job;
    private List<QuestionAndAnswers> questionsAndAnswers;

    public Application() {
    }

    public Application(Integer id, String appliedDate, Status status, Integer rating, Applicant applicant, Job job) {
        this.id = id;
        this.appliedDate = appliedDate;
        this.status = status;
        this.rating = rating;
        this.applicant = applicant;
        this.job = job;
    }

    public List<QuestionAndAnswers> getQuestionsAndAnswers() {
        return questionsAndAnswers;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(String appliedDate) {
        this.appliedDate = appliedDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    @Override
    public String toString() {
        return "Application{" +
                "id=" + id +
                ", appliedDate='" + appliedDate + '\'' +
                ", status=" + status +
                ", rating=" + rating +
                ", applicant=" + applicant +
                ", job=" + job +
                ", questionsAndAnswers=" + questionsAndAnswers +
                '}';
    }
}
