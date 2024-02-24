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
    private static final String APPLICATIONS_URL = "applications";
    private static final String JOBS_URL = "jobs";

    private static final String OPEN_JOB_STATUS = "open";
    private static final String DRAFT_JOB_STATUS = "draft";
    private static final String OPEN_AND_DRAFT_STATUS = "DRAFT_AND_OPEN";

    private static final String STATUS_NOT_A_FIT = "10";

    public static void main(String[] args) throws IOException {
        Config config = new Config();
        final String TOKEN = config.getToken();
        final String AUTHORIZATION_HEADER = config.getAuthHeader();
        final String DOMAIN = config.getDomain();
        final String BASE_URL = "https://api.bamboohr.com/api/gateway.php/" + DOMAIN + "/v1/applicant_tracking/";

        List<Job> jobs;
        String jobBody = makeApiRequest(AUTHORIZATION_HEADER, buildJobListingUrl(BASE_URL, OPEN_JOB_STATUS));
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
        String applicationUrl = buildApplicationsUrl(BASE_URL, String.valueOf(selectedJobId));
        String applicationJson = makeApiRequest(AUTHORIZATION_HEADER, applicationUrl);
        ApplicationResponse applicationResponse = objectMapper.readValue(applicationJson, ApplicationResponse.class);
        List<Application> applications = applicationResponse.getApplications();


        //loop through them 1 by 1, show some details, questions answered total, reference
        //not handling pagination right now
        for (Application application : applications) {

            String applicationDetailsUrl = buildApplicationDetailsUrl(BASE_URL, application.getId().toString());
            String applicationDetailJson = makeApiRequest(AUTHORIZATION_HEADER, applicationDetailsUrl);
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
                    Response response = changeStatus(AUTHORIZATION_HEADER, DOMAIN, application.getId().toString(), STATUS_NOT_A_FIT);
                    if (response.isSuccessful()) {
                        System.out.println("Successfully set status");
                    } else {
                        System.out.println("Did not set status");
                    }
                    break;
                } else if ("O".equalsIgnoreCase(input)) {
                    inputChar = input;
                    try {
                        openPage(bambooPage(DOMAIN, application.getId().toString()));
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

    private static String buildJobListingUrl(String baseUrl, String status) {
        return baseUrl + JOBS_URL + "?statusGroups" + status + "&sortBy=title";
    }

    private static String buildApplicationsUrl(String baseUrl, String jobId) {
        return baseUrl+APPLICATIONS_URL+"?jobId="+jobId+"&applicationStatus=NEW&sortBy=created_date";
    }

    private static String buildApplicationDetailsUrl(String baseUrl, String applicationId) {
        return baseUrl+APPLICATIONS_URL+"/"+applicationId;
    }

    private static String bambooPage(String domain, String applicantId) {
        return "https://"+ domain + ".bamboohr.com/hiring/candidates/"+applicantId+ "?list_type=jobs#ats-comments";
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

    private static Response changeStatus(String authorizationHeader, String domain, String applicantionId, String status) throws IOException {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create("{\"status\":"+status+"}", mediaType);
        Request request = new Request.Builder()
                .url("https://api.bamboohr.com/api/gateway.php/"+domain+"/v1/applicant_tracking/applications/"+applicantionId+"/status")
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", authorizationHeader)
                .build();

        return client.newCall(request).execute();
    }

    private static String makeApiRequest(String authorizationHeader, String urlRequest) throws IOException {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(urlRequest)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("authorization", authorizationHeader)
                .build();


        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            assert response.body() != null;
            return response.body().string();
        }
    }
}
