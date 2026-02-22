package hu.vszili.survey.service;

import hu.vszili.survey.data.Member;
import hu.vszili.survey.data.SurveyStatistic;

import java.util.List;

public interface SurveyService {

    List<Member> getMembersCompleted(Long surveyId);

    List<Member> getEligibleMembers(Long surveyId);

    List<SurveyStatistic> getStatistics();

}
