package hu.vszili.survey.service;

import hu.vszili.survey.data.Member;
import hu.vszili.survey.data.Participation;
import hu.vszili.survey.data.SurveyStatistic;
import hu.vszili.survey.exception.SurveyNotFoundException;
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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SurveyServiceTest {
    
    @Mock
    private SurveyRepository surveyRepository;
    
    @Mock
    private StatusRepository statusRepository;

    @Mock
    private ParticipationRepository participationRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private SurveyService surveyService;

    /**
     * Retrieves a list of completed surveys for a specific member.
     */
    @Test
    void testGetMembersCompleted_SurveyExists_ReturnsMembersWithCompletedStatus() {
        // given
        Long surveyId = 1L;
        Long completedStatusId = 1L;

        when(surveyRepository.existsById(surveyId)).thenReturn(true);
        when(statusRepository.findIdByName("Completed")).thenReturn(completedStatusId);

        Participation participation1 = new Participation();
        participation1.setMemberId(1L);
        participation1.setSurveyId(surveyId);
        participation1.setStatus(completedStatusId);

        Participation participation2 = new Participation();
        participation2.setMemberId(2L);
        participation2.setSurveyId(surveyId);
        participation2.setStatus(completedStatusId);

        when(participationRepository.findAll()).thenReturn(Arrays.asList(participation1, participation2));

        Member member1 = new Member();
        member1.setId(1L);
        member1.setFullName("John Doe");

        Member member2 = new Member();
        member2.setId(2L);
        member2.setFullName("Jane Smith");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(member2));

        // when
        List<Member> members = surveyService.getMembersCompleted(surveyId);

        // then
        Assertions.assertEquals(2, members.size());
        Assertions.assertTrue(members.contains(member1));
        Assertions.assertTrue(members.contains(member2));
    }

    /**
     * Retrieves a list of completed surveys for a specific member.
     */
    @Test
    void testGetMembersCompleted_SurveyNotFound_ThrowsException() {
        // given
        Long surveyId = 99L;
        when(surveyRepository.existsById(surveyId)).thenReturn(false);

        // when-then
        Assertions.assertThrows(SurveyNotFoundException.class, () -> surveyService.getMembersCompleted(surveyId));
    }

    /**
     * Retrieves a list of completed surveys for a specific member.
     */
    @Test
    void testGetMembersCompleted_NoCompletedParticipations_ReturnsEmptyList() {
        // given
        Long surveyId = 1L;
        Long completedStatusId = 1L;

        when(surveyRepository.existsById(surveyId)).thenReturn(true);
        when(statusRepository.findIdByName("Completed")).thenReturn(completedStatusId);
        when(participationRepository.findAll()).thenReturn(Collections.emptyList());

        // when
        List<Member> members = surveyService.getMembersCompleted(surveyId);

        // then
        Assertions.assertTrue(members.isEmpty());
    }

    /**
     * Retrieves a list of completed surveys for a specific member.
     */
    @Test
    void testGetMembersCompleted_OnlyCompletedStatusIncluded() {
        // given
        Long surveyId = 1L;
        Long completedStatusId = 1L;
        Long rejectedStatusId = 3L;

        when(surveyRepository.existsById(surveyId)).thenReturn(true);
        when(statusRepository.findIdByName("Completed")).thenReturn(completedStatusId);

        Participation completed = new Participation();
        completed.setMemberId(1L);
        completed.setSurveyId(surveyId);
        completed.setStatus(completedStatusId);

        Participation rejected = new Participation();
        rejected.setMemberId(2L);
        rejected.setSurveyId(surveyId);
        rejected.setStatus(rejectedStatusId);

        when(participationRepository.findAll()).thenReturn(Arrays.asList(completed, rejected));

        Member member1 = new Member();
        member1.setId(1L);
        member1.setFullName("John Doe");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));

        // when
        List<Member> members = surveyService.getMembersCompleted(surveyId);

        // then
        Assertions.assertEquals(1, members.size());
        Assertions.assertTrue(members.contains(member1));
    }

    /**
     * Retrieves a list of completed surveys for a specific member.
     */
    @Test
    void testGetMembersCompleted_MemberNotFound_Ignored() {
        // given
        Long surveyId = 1L;
        Long completedStatusId = 1L;

        when(surveyRepository.existsById(surveyId)).thenReturn(true);
        when(statusRepository.findIdByName("Completed")).thenReturn(completedStatusId);

        Participation participation = new Participation();
        participation.setMemberId(999L);
        participation.setSurveyId(surveyId);
        participation.setStatus(completedStatusId);

        when(participationRepository.findAll()).thenReturn(Collections.singletonList(participation));
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        // when
        List<Member> members = surveyService.getMembersCompleted(surveyId);

        // then
        Assertions.assertTrue(members.isEmpty());
    }

    /**
     * Retrieves a list of eligible members for a survey, excluding those who have already participated.
     */
    @Test
    void testGetEligibleMembers_ReturnsActiveMembersWithoutParticipation() {
        // given
        Long surveyId = 1L;
        Long notAskedStatusId = 0L;

        when(statusRepository.findIdByName("Not asked")).thenReturn(notAskedStatusId);

        Member activeMember1 = new Member();
        activeMember1.setId(1L);
        activeMember1.setFullName("John Doe");
        activeMember1.setActive(true);

        Member activeMember2 = new Member();
        activeMember2.setId(2L);
        activeMember2.setFullName("Jane Smith");
        activeMember2.setActive(true);

        Member inactiveMember = new Member();
        inactiveMember.setId(3L);
        inactiveMember.setFullName("Bob Inactive");
        inactiveMember.setActive(false);

        Map<Long, Member> allMembers = new ConcurrentHashMap<>();
        allMembers.put(1L, activeMember1);
        allMembers.put(2L, activeMember2);
        allMembers.put(3L, inactiveMember);

        when(memberRepository.getMembers()).thenReturn(allMembers);

        Participation participation = new Participation();
        participation.setMemberId(1L);
        participation.setSurveyId(surveyId);
        participation.setStatus(1L);

        when(participationRepository.findAll()).thenReturn(Collections.singletonList(participation));

        // when
        List<Member> eligibleMembers = surveyService.getEligibleMembers(surveyId);

        // then
        Assertions.assertEquals(1, eligibleMembers.size());
        Assertions.assertTrue(eligibleMembers.contains(activeMember2));
    }

    /**
     * Retrieves a list of eligible members for a survey, excluding those who have already participated.
     */
    @Test
    void testGetEligibleMembers_ExcludesInactiveMembers() {
        // given
        Long surveyId = 1L;
        Long notAskedStatusId = 0L;

        when(statusRepository.findIdByName("Not asked")).thenReturn(notAskedStatusId);

        Member activeMember = new Member();
        activeMember.setId(1L);
        activeMember.setFullName("John Doe");
        activeMember.setActive(true);

        Member inactiveMember = new Member();
        inactiveMember.setId(2L);
        inactiveMember.setFullName("Bob Inactive");
        inactiveMember.setActive(false);

        Map<Long, Member> allMembers = new ConcurrentHashMap<>();
        allMembers.put(1L, activeMember);
        allMembers.put(2L, inactiveMember);

        when(memberRepository.getMembers()).thenReturn(allMembers);
        when(participationRepository.findAll()).thenReturn(Collections.emptyList());

        // when
        List<Member> eligibleMembers = surveyService.getEligibleMembers(surveyId);

        // then
        Assertions.assertEquals(1, eligibleMembers.size());
        Assertions.assertTrue(eligibleMembers.contains(activeMember));
    }

    /**
     * Retrieves a list of eligible members for a survey, excluding those who have already participated.
     */
    @Test
    void testGetEligibleMembers_NoEligibleMembers_ReturnsEmptyList() {
        // given
        Long surveyId = 1L;
        Long notAskedStatusId = 0L;

        when(statusRepository.findIdByName("Not asked")).thenReturn(notAskedStatusId);

        Member inactiveMember = new Member();
        inactiveMember.setId(1L);
        inactiveMember.setFullName("Bob Inactive");
        inactiveMember.setActive(false);

        Map<Long, Member> allMembers = new ConcurrentHashMap<>();
        allMembers.put(1L, inactiveMember);

        when(memberRepository.getMembers()).thenReturn(allMembers);
        when(participationRepository.findAll()).thenReturn(Collections.emptyList());

        // when
        List<Member> eligibleMembers = surveyService.getEligibleMembers(surveyId);

        // then
        Assertions.assertTrue(eligibleMembers.isEmpty());
    }

    /**
     * Retrieves survey statistics for a given survey.
     */
    @Test
    void testGetStatistics_ReturnsSurveyStatistics() {
        // given
        Long completedStatusId = 1L;
        Long filteredStatusId = 2L;
        Long rejectedStatusId = 3L;

        when(statusRepository.findIdByName("Completed")).thenReturn(completedStatusId);
        when(statusRepository.findIdByName("Filtered")).thenReturn(filteredStatusId);
        when(statusRepository.findIdByName("Rejected")).thenReturn(rejectedStatusId);

        Participation p1 = new Participation();
        p1.setMemberId(1L);
        p1.setSurveyId(1L);
        p1.setStatus(completedStatusId);
        p1.setLength(10);

        Participation p2 = new Participation();
        p2.setMemberId(2L);
        p2.setSurveyId(1L);
        p2.setStatus(completedStatusId);
        p2.setLength(20);

        Participation p3 = new Participation();
        p3.setMemberId(3L);
        p3.setSurveyId(1L);
        p3.setStatus(filteredStatusId);
        p3.setLength(15);

        when(participationRepository.findAll()).thenReturn(Arrays.asList(p1, p2, p3));
        when(surveyRepository.getNameById(1L)).thenReturn("Survey Name");

        // when
        List<SurveyStatistic> statistics = surveyService.getStatistics();

        // then
        Assertions.assertEquals(1, statistics.size());
        SurveyStatistic stat = statistics.getFirst();
        Assertions.assertEquals(1L, stat.getId());
        Assertions.assertEquals("Survey Name", stat.getName());
        Assertions.assertEquals(2, stat.getCompletedCount());
        Assertions.assertEquals(1, stat.getFilteredCount());
        Assertions.assertEquals(0, stat.getRejectedCount());
        Assertions.assertEquals(15.0, stat.getAverageLength());
    }

    /**
     * Retrieves survey statistics for a given survey.
     */
    @Test
    void testGetStatistics_MultipleSurveys() {
        // given
        Long completedStatusId = 1L;
        Long filteredStatusId = 2L;
        Long rejectedStatusId = 3L;

        when(statusRepository.findIdByName("Completed")).thenReturn(completedStatusId);
        when(statusRepository.findIdByName("Filtered")).thenReturn(filteredStatusId);
        when(statusRepository.findIdByName("Rejected")).thenReturn(rejectedStatusId);

        Participation p1 = new Participation();
        p1.setMemberId(1L);
        p1.setSurveyId(1L);
        p1.setStatus(completedStatusId);
        p1.setLength(10);

        Participation p2 = new Participation();
        p2.setMemberId(1L);
        p2.setSurveyId(2L);
        p2.setStatus(filteredStatusId);
        p2.setLength(5);

        when(participationRepository.findAll()).thenReturn(Arrays.asList(p1, p2));
        when(surveyRepository.getNameById(1L)).thenReturn("Survey 1");
        when(surveyRepository.getNameById(2L)).thenReturn("Survey 2");

        // when
        List<SurveyStatistic> statistics = surveyService.getStatistics();

        // then
        Assertions.assertEquals(2, statistics.size());
    }

    /**
     * Retrieves survey statistics for a given survey.
     */
    @Test
    void testGetStatistics_NoParticipation_ReturnsEmptyList() {
        // given
        Long completedStatusId = 1L;
        Long filteredStatusId = 2L;
        Long rejectedStatusId = 3L;

        when(statusRepository.findIdByName("Completed")).thenReturn(completedStatusId);
        when(statusRepository.findIdByName("Filtered")).thenReturn(filteredStatusId);
        when(statusRepository.findIdByName("Rejected")).thenReturn(rejectedStatusId);
        when(participationRepository.findAll()).thenReturn(Collections.emptyList());

        // when
        List<SurveyStatistic> statistics = surveyService.getStatistics();

        // then
        Assertions.assertTrue(statistics.isEmpty());
    }

    /**
     * Retrieves survey statistics for a given survey.
     */
    @Test
    void testGetStatistics_NullLengthIgnored() {
        // given
        Long completedStatusId = 1L;
        Long filteredStatusId = 2L;
        Long rejectedStatusId = 3L;

        when(statusRepository.findIdByName("Completed")).thenReturn(completedStatusId);
        when(statusRepository.findIdByName("Filtered")).thenReturn(filteredStatusId);
        when(statusRepository.findIdByName("Rejected")).thenReturn(rejectedStatusId);

        Participation p1 = new Participation();
        p1.setMemberId(1L);
        p1.setSurveyId(1L);
        p1.setStatus(completedStatusId);
        p1.setLength(10);

        Participation p2 = new Participation();
        p2.setMemberId(2L);
        p2.setSurveyId(1L);
        p2.setStatus(completedStatusId);
        p2.setLength(null);

        when(participationRepository.findAll()).thenReturn(Arrays.asList(p1, p2));
        when(surveyRepository.getNameById(1L)).thenReturn("Survey Name");

        // when
        List<SurveyStatistic> statistics = surveyService.getStatistics();

        // then
        Assertions.assertEquals(1, statistics.size());
        SurveyStatistic stat = statistics.getFirst();
        Assertions.assertEquals(10.0, stat.getAverageLength());
    }

    /**
     * Retrieves survey statistics for a given survey.
     */
    @Test
    void testGetStatistics_AllNullLengths_AverageIsZero() {
        // given
        Long completedStatusId = 1L;
        Long filteredStatusId = 2L;
        Long rejectedStatusId = 3L;

        when(statusRepository.findIdByName("Completed")).thenReturn(completedStatusId);
        when(statusRepository.findIdByName("Filtered")).thenReturn(filteredStatusId);
        when(statusRepository.findIdByName("Rejected")).thenReturn(rejectedStatusId);

        Participation p1 = new Participation();
        p1.setMemberId(1L);
        p1.setSurveyId(1L);
        p1.setStatus(completedStatusId);
        p1.setLength(null);

        when(participationRepository.findAll()).thenReturn(Collections.singletonList(p1));
        when(surveyRepository.getNameById(1L)).thenReturn("Survey Name");

        // when
        List<SurveyStatistic> statistics = surveyService.getStatistics();

        // then
        Assertions.assertEquals(1, statistics.size());
        SurveyStatistic stat = statistics.getFirst();
        Assertions.assertEquals(0.0, stat.getAverageLength());
    }
}
