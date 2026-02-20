package hu.vszili.survey.service;

import hu.vszili.survey.data.Participation;
import hu.vszili.survey.data.Survey;
import hu.vszili.survey.exception.MemberNotFoundException;
import hu.vszili.survey.repositroy.MemberRepository;
import hu.vszili.survey.repositroy.ParticipationRepository;
import hu.vszili.survey.repositroy.StatusRepository;
import hu.vszili.survey.repositroy.SurveyRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SurveyRepository surveyRepository;

    @Mock
    private ParticipationRepository participationRepository;

    @Mock
    private StatusRepository statusRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    void testGetCompletedSurveys_MemberExists_SurveysReturned() {
        // given
        Long memberId = 1L;
        Long completedStatusId = 1L;

        when(memberRepository.existsById(memberId)).thenReturn(true);
        when(statusRepository.findIdByName("Completed")).thenReturn(completedStatusId);

        Participation participation1 = new Participation();
        participation1.setMemberId(memberId);
        participation1.setSurveyId(1L);
        participation1.setStatus(completedStatusId);

        Participation participation2 = new Participation();
        participation2.setMemberId(memberId);
        participation2.setSurveyId(2L);
        participation2.setStatus(completedStatusId);

        when(participationRepository.findAll()).thenReturn(Arrays.asList(participation1, participation2));

        Survey survey1 = new Survey(1L, "Survey A", 100, 10, 2);
        Survey survey2 = new Survey(2L, "Survey B", 200, 20, 5);

        when(surveyRepository.findById(1L)).thenReturn(Optional.of(survey1));
        when(surveyRepository.findById(2L)).thenReturn(Optional.of(survey2));

        // when
        List<Survey> completedSurveys = memberService.getCompletedSurveys(memberId);

        // then
        Assertions.assertEquals(2, completedSurveys.size());
        Assertions.assertTrue(completedSurveys.contains(survey1));
        Assertions.assertTrue(completedSurveys.contains(survey2));
    }

    @Test
    void testGetCompletedSurveys_MemberExists_NoSurveys() {
        // given
        Long memberId = 1L;
        Long completedStatusId = 1L;

        when(memberRepository.existsById(memberId)).thenReturn(true);
        when(statusRepository.findIdByName("Completed")).thenReturn(completedStatusId);
        when(participationRepository.findAll()).thenReturn(Collections.emptyList());

        // when
        List<Survey> completedSurveys = memberService.getCompletedSurveys(memberId);

        // then
        Assertions.assertTrue(completedSurveys.isEmpty());
    }

    @Test
    void testGetCompletedSurveys_MemberNotFound_ThrowsException() {
        // given
        Long memberId = 99L;
        when(memberRepository.existsById(memberId)).thenReturn(false);

        // when-then
        Assertions.assertThrows(MemberNotFoundException.class, () -> memberService.getCompletedSurveys(memberId));
    }

    @Test
    void testGetCompletedSurveys_ParticipationWithNoSurvey_Ignored() {
        // given
        Long memberId = 1L;
        Long completedStatusId = 1L;

        when(memberRepository.existsById(memberId)).thenReturn(true);
        when(statusRepository.findIdByName("Completed")).thenReturn(completedStatusId);

        Participation participation1 = new Participation();
        participation1.setMemberId(memberId);
        participation1.setSurveyId(1L);
        participation1.setStatus(completedStatusId);

        Participation participation2 = new Participation();
        participation2.setMemberId(memberId);
        participation2.setSurveyId(999L);
        participation2.setStatus(completedStatusId);

        when(participationRepository.findAll()).thenReturn(Arrays.asList(participation1, participation2));

        Survey survey1 = new Survey(1L, "Survey A", 100, 10, 2);
        when(surveyRepository.findById(1L)).thenReturn(Optional.of(survey1));
        when(surveyRepository.findById(999L)).thenReturn(Optional.empty());

        // when
        List<Survey> completedSurveys = memberService.getCompletedSurveys(memberId);

        // then
        Assertions.assertEquals(1, completedSurveys.size());
        Assertions.assertTrue(completedSurveys.contains(survey1));
    }
}