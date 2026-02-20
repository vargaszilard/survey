package hu.vszili.survey.exception;

public class SurveyNotFoundException extends RuntimeException {
    public SurveyNotFoundException(Long surveyId) {
        super(String.format("Survey with id %s not found", surveyId));
    }
}
