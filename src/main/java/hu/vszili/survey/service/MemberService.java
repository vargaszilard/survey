package hu.vszili.survey.service;

import hu.vszili.survey.data.Survey;

import java.util.List;

public interface MemberService {

    List<Survey> getCompletedSurveys(Long memberId);

    Integer getPoints(Long memberId);

}
