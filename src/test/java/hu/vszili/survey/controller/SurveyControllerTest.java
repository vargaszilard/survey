package hu.vszili.survey.controller;

import hu.vszili.survey.data.Member;
import hu.vszili.survey.data.SurveyStatistic;
import hu.vszili.survey.exception.SurveyNotFoundException;
import hu.vszili.survey.service.SurveyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SurveyController.class)
class SurveyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SurveyService surveyService;

    /**
     * Test case: Valid surveyId, members who completed survey exist.
     */
    @Test
    void testGetCompletedMembers_ValidSurveyId_ReturnsCompletedMembers() throws Exception {
        // given
        Long surveyId = 1L;

        List<Member> completedMembers = List.of(
                createMember(1L, "John Doe", "john.doe@example.com", true),
                createMember(2L, "Jane Smith", "jane.smith@example.com", true)
        );

        when(surveyService.getMembersCompleted(surveyId)).thenReturn(completedMembers);

        // when-then
        mockMvc.perform(get("/api/surveys/{surveyId}/members/completed", surveyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].fullName").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].fullName").value("Jane Smith"))
                .andExpect(jsonPath("$[1].email").value("jane.smith@example.com"))
                .andExpect(jsonPath("$[1].active").value(true));
    }

    /**
     * Test case: Valid surveyId, no members have completed survey.
     */
    @Test
    void testGetCompletedMembers_ValidSurveyId_NoMembersCompleted() throws Exception {
        // given
        Long surveyId = 2L;

        when(surveyService.getMembersCompleted(surveyId)).thenReturn(List.of());

        // when-then
        mockMvc.perform(get("/api/surveys/{surveyId}/members/completed", surveyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    /**
     * Test case: Invalid surveyId, survey does not exist.
     */
    @Test
    void testGetCompletedMembers_InvalidSurveyId_ThrowsNotFound() throws Exception {
        // given
        Long surveyId = 3L;

        when(surveyService.getMembersCompleted(surveyId)).thenThrow(new SurveyNotFoundException(surveyId));

        // when-then
        mockMvc.perform(get("/api/surveys/{surveyId}/members/completed", surveyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Test case: Valid surveyId, eligible members exist.
     */
    @Test
    void testGetEligibleMembers_ValidSurveyId_ReturnsEligibleMembers() throws Exception {
        // given
        Long surveyId = 1L;

        List<Member> eligibleMembers = List.of(
                createMember(1L, "Alice Doe", "alice.doe@example.com", true),
                createMember(2L, "Bob Smith", "bob.smith@example.com", true)
        );

        when(surveyService.getEligibleMembers(surveyId)).thenReturn(eligibleMembers);

        // when-then
        mockMvc.perform(get("/api/surveys/{surveyId}/eligible-members", surveyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].fullName").value("Alice Doe"))
                .andExpect(jsonPath("$[0].email").value("alice.doe@example.com"))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].fullName").value("Bob Smith"))
                .andExpect(jsonPath("$[1].email").value("bob.smith@example.com"))
                .andExpect(jsonPath("$[1].active").value(true));
    }

    /**
     * Test case: Valid surveyId, no eligible members.
     */
    @Test
    void testGetEligibleMembers_ValidSurveyId_NoEligibleMembers() throws Exception {
        // given
        Long surveyId = 2L;

        when(surveyService.getEligibleMembers(surveyId)).thenReturn(List.of());

        // when-then
        mockMvc.perform(get("/api/surveys/{surveyId}/eligible-members", surveyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    /**
     * Test case: Invalid surveyId, survey does not exist.
     */
    @Test
    void testGetEligibleMembers_InvalidSurveyId_ThrowsNotFound() throws Exception {
        // given
        Long surveyId = 3L;

        when(surveyService.getEligibleMembers(surveyId)).thenThrow(new SurveyNotFoundException(surveyId));

        // when-then
        mockMvc.perform(get("/api/surveys/{surveyId}/eligible-members", surveyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Test case: Survey statistics exist.
     */
    @Test
    void testGetSurveyStatistics_ReturnsSurveyStatistics() throws Exception {
        // given
        List<SurveyStatistic> mockStatistics = Arrays.asList(
                SurveyStatistic.builder()
                        .id(1L)
                        .name("Survey A")
                        .completedCount(50L)
                        .filteredCount(10L)
                        .rejectedCount(5L)
                        .averageLength(15.5)
                        .build(),
                SurveyStatistic.builder()
                        .id(2L)
                        .name("Survey B")
                        .completedCount(75L)
                        .filteredCount(15L)
                        .rejectedCount(10L)
                        .averageLength(20.3)
                        .build()
        );

        when(surveyService.getStatistics()).thenReturn(mockStatistics);

        // when-then
        mockMvc.perform(get("/api/surveys/statistics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Survey A"))
                .andExpect(jsonPath("$[0].completedCount").value(50))
                .andExpect(jsonPath("$[0].filteredCount").value(10))
                .andExpect(jsonPath("$[0].rejectedCount").value(5))
                .andExpect(jsonPath("$[0].averageLength").value(15.5))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Survey B"))
                .andExpect(jsonPath("$[1].completedCount").value(75))
                .andExpect(jsonPath("$[1].filteredCount").value(15))
                .andExpect(jsonPath("$[1].rejectedCount").value(10))
                .andExpect(jsonPath("$[1].averageLength").value(20.3));
    }

    /**
     * Test case: No statistics exist.
     */
    @Test
    void testGetSurveyStatistics_NoStatistics_ReturnsEmptyList() throws Exception {
        // given
        when(surveyService.getStatistics()).thenReturn(Collections.emptyList());

        // when-then
        mockMvc.perform(get("/api/surveys/statistics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    /**
     * Test case: Single survey statistics exist.
     */
    @Test
    void testGetSurveyStatistics_SingleSurvey() throws Exception {
        // given
        List<SurveyStatistic> mockStatistics = Collections.singletonList(
                SurveyStatistic.builder()
                        .id(1L)
                        .name("Survey A")
                        .completedCount(100L)
                        .filteredCount(20L)
                        .rejectedCount(15L)
                        .averageLength(18.5)
                        .build()
        );

        when(surveyService.getStatistics()).thenReturn(mockStatistics);

        // when-then
        mockMvc.perform(get("/api/surveys/statistics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Survey A"))
                .andExpect(jsonPath("$[0].completedCount").value(100))
                .andExpect(jsonPath("$[0].filteredCount").value(20))
                .andExpect(jsonPath("$[0].rejectedCount").value(15))
                .andExpect(jsonPath("$[0].averageLength").value(18.5));
    }

    /**
     * Test case: Survey statistics exist, but all values are zero.
     */
    @Test
    void testGetSurveyStatistics_ZeroStatistics() throws Exception {
        // given
        List<SurveyStatistic> mockStatistics = Collections.singletonList(
                SurveyStatistic.builder()
                        .id(1L)
                        .name("Survey A")
                        .completedCount(0L)
                        .filteredCount(0L)
                        .rejectedCount(0L)
                        .averageLength(0.0)
                        .build()
        );

        when(surveyService.getStatistics()).thenReturn(mockStatistics);

        // when-then
        mockMvc.perform(get("/api/surveys/statistics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].completedCount").value(0))
                .andExpect(jsonPath("$[0].filteredCount").value(0))
                .andExpect(jsonPath("$[0].rejectedCount").value(0))
                .andExpect(jsonPath("$[0].averageLength").value(0.0));
    }

    private Member createMember(Long id, String fullName, String email, Boolean active) {
        Member member = new Member();
        member.setId(id);
        member.setFullName(fullName);
        member.setEmail(email);
        member.setActive(active);
        return member;
    }
}