package Bamboo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionAndAnswers {
    private Question question;
    private Answer answer;

    public Question getQuestion() {
        return question;
    }

    public Answer getAnswer() {
        return answer;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Question {
        private int id;
        private String label;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Answer {
        private int id;
        private String label;
    }
}


