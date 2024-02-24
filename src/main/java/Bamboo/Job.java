package Bamboo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Job {

    private int id;
    private Title title;
    private Status status;
    private int newApplicantsCount;

    public int getNewApplicantsCount() {return newApplicantsCount;}
    public int getId() {
        return id;
    }
    public Title getTitle() {
        return title;
    }
    public Status getStatus() {return status;}
}
