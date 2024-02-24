package org.example;

import Bamboo.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;


public class Main {
    private static final String BASE_URL = "https://api.bamboohr.com/api/gateway.php/domain/v1/applicant_tracking/";
    private static final String APPLICATIONS_URL = "applications";
    private static final String JOBS_URL = "jobs";
    private static final String token = "secret";
    private static final String AUTHORIZATION_HEADER = "dont commit secrets";

    private static final String OPEN_JOB_STATUS = "open";
    private static final String DRAFT_JOB_STATUS = "draft";
    private static final String OPEN_AND_DRAFT_STATUS = "DRAFT_AND_OPEN";
    


    private static final String STATUS_NOT_A_FIT = "10";

    public static void main(String[] args) throws IOException {

        List<Job> jobs;
        String jobBody = makeApiRequest(buildJobListingUrl(OPEN_JOB_STATUS));
        final ObjectMapper objectMapper = new ObjectMapper();
        jobs = objectMapper.readValue(jobBody, new TypeReference<>() {});

        Scanner scanner = new Scanner(System.in);

        for (int i = 0; i<jobs.size(); i++) {
            Job job = jobs.get(i);
            String output = String.format("%-40s %10s %15s",
                    (i + 1) + ": " + job.getTitle().getLabel(),
                    job.getNewApplicantsCount(),
                    job.getStatus().getLabel());
            System.out.println(output);
        }

        //can prob refactor this ya?
        int selectedJobId;
        while (true) {
            System.out.println("Choose a job listing to get applicants: ");
            try {
                int jobIndex = scanner.nextInt();
                Job selectedJob;
                if (jobIndex>0 && jobIndex<jobs.size()) {
                    selectedJob = jobs.get(jobIndex-1);
                    System.out.println("You selected: " + selectedJob.getTitle().getLabel());
                    selectedJobId = selectedJob.getId();
                    break;
                } else {
                    System.out.println("Invalid Choice");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid Input, select a number");
                scanner.next();
            }
        }

        //53
        //aplicant id 14653

        //then get all applicants for that job listing
        String applicationUrl = buildApplicationsUrl(String.valueOf(selectedJobId));
        String applicationJson = makeApiRequest(applicationUrl);
        ApplicationResponse applicationResponse = objectMapper.readValue(applicationJson, ApplicationResponse.class);
        List<Application> applications = applicationResponse.getApplications();


        //loop through them 1 by 1, show some details, questions answered total, reference
        //not handling pagination right now
        for (Application application : applications) {

            String applicationDetailsUrl = buildApplicationDetailsUrl(application.getId().toString());
            String applicationDetailJson = makeApiRequest(applicationDetailsUrl);
            Application applicationDetails = objectMapper.readValue(applicationDetailJson, Application.class);

            Applicant applicant = application.getApplicant();
            final String output = String.format("%-30s Status: %-15s Q&A Count: %d",
                    applicant.getFirstName() + " " + applicant.getLastName(), // Concatenate first name and last name
                    applicationDetails.getStatus().getLabel(),
                    applicationDetails.getQuestionsAndAnswers().size()
            );
            System.out.println(output);
            System.out.println("S: Skip - N: Not a fit - O: Open Page");
            String inputChar;
            while (true) {
                String input = scanner.next();
                if ("N".equalsIgnoreCase(input)) {
                    inputChar = input;
                    Response response = changeStatus(application.getId().toString(), STATUS_NOT_A_FIT);
                    if (response.isSuccessful()) {
                        System.out.println("Successfully set status");
                    } else {
                        System.out.println("Did not set status");
                    }
                    break;
                } else if ("O".equalsIgnoreCase(input)) {
                    inputChar = input;
                    try {
                        openPage(bambooPage(application.getId().toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                } else if ("S".equalsIgnoreCase(input)) {
                    break;
                }
                System.out.println("Invalid Input");
            }
        }

        //ask, open page or update status to NOT A FIT
        //status not a fit == 10
        //update status or open browser

    }

    private static String buildJobListingUrl(String status) {
        return BASE_URL + JOBS_URL + "?statusGroups" + status + "&sortBy=title";
    }

    private static String buildApplicationsUrl(String jobId) {
        return BASE_URL+APPLICATIONS_URL+"?jobId="+jobId+"&applicationStatus=NEW&sortBy=created_date";
    }

    private static String buildApplicationDetailsUrl(String applicationId) {
        return BASE_URL+APPLICATIONS_URL+"/"+applicationId;
    }

    private static String bambooPage(String applicantId) {
        return "https://domain.bamboohr.com/hiring/candidates/"+applicantId+ "?list_type=jobs#ats-comments";
    }

    private static void openPage(String url) throws IOException, URISyntaxException {
        try {
            URI uri = new URI(url);
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static Response changeStatus(String applicantionId, String status) throws IOException {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create("{\"status\":"+status+"}", mediaType);
        Request request = new Request.Builder()
                .url("https://api.bamboohr.com/api/gateway.php/domain/v1/applicant_tracking/applications/"+applicantionId+"/status")
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Basic dont commit secrets")
                .build();

        return client.newCall(request).execute();
    }

    private static String makeApiRequest(String urlRequest) throws IOException {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(urlRequest)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("authorization", AUTHORIZATION_HEADER)
                .build();


        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            assert response.body() != null;
            return response.body().string();
        }
    }
}
