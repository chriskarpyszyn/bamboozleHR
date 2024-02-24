package Bamboo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationsResponse {

    private Boolean paginationComplete;
    private List<Application> applications;
    private String nextPageUrl;

    public ApplicationsResponse() {
    }

    public ApplicationsResponse(Boolean paginationComplete, List<Application> applications, String nextPageUrl) {
        this.paginationComplete = paginationComplete;
        this.applications = applications;
        this.nextPageUrl = nextPageUrl;
    }

    public Boolean getPaginationComplete() {
        return paginationComplete;
    }

    public void setPaginationComplete(Boolean paginationComplete) {
        this.paginationComplete = paginationComplete;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }

    public String getNextPageUrl() {
        return nextPageUrl;
    }

    public void setNextPageUrl(String nextPageUrl) {
        this.nextPageUrl = nextPageUrl;
    }
}
