package hu.vszili.survey.repositroy;

import hu.vszili.survey.data.Survey;

import java.util.Optional;

public interface SurveyRepository {

    boolean existsById(Long surveyId);

    Optional<Survey> findById(Long surveyId);

    Integer getPoints(Long surveyId, Boolean isComplete);

    String getNameById(Long surveyId);

}
