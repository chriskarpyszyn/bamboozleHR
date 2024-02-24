package Bamboo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationResponse {
    List<Application> applications;

    public List<Application> getApplications() {
        return applications;
    }
}
