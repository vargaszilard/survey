package hu.vszili.survey.controller;

import hu.vszili.survey.data.Survey;
import hu.vszili.survey.exception.MemberNotFoundException;
import hu.vszili.survey.service.MemberService;
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
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    /**
     * Test case: Valid member ID, surveys exist.
     * Expected behavior: Returns 200 status and list of completed surveys.
     */
    @Test
    void testGetCompletedSurveys_ValidMemberId_ReturnsSurveys() throws Exception {
        // given
        List<Survey> mockSurveys = Arrays.asList(
                new Survey(1L, "Survey A", 100, 10, 2),
                new Survey(2L, "Survey B", 200, 20, 5)
        );

        when(memberService.getCompletedSurveys(1L)).thenReturn(mockSurveys);

        // when-then
        mockMvc.perform(get("/api/members/1/surveys/completed")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Survey A")))
                .andExpect(jsonPath("$[0].expectedCompletes", is(100)))
                .andExpect(jsonPath("$[0].completionPoints", is(10)))
                .andExpect(jsonPath("$[0].filteredPoint", is(2)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Survey B")));
    }

    /**
     * Test case: Valid member ID, no surveys exist.
     * Expected behavior: Returns 200 status and an empty list.
     */
    @Test
    void testGetCompletedSurveys_NoSurveys_ReturnsEmptyList() throws Exception {
        // given
        when(memberService.getCompletedSurveys(1L)).thenReturn(Collections.emptyList());

        // when-then
        mockMvc.perform(get("/api/members/1/surveys/completed")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    /**
     * Test case: Invalid member ID, member does not exist.
     * Expected behavior: Returns 404 status with error message.
     */
    @Test
    void testGetCompletedSurveys_InvalidMemberId_ReturnsNotFound() throws Exception {
        // given
        when(memberService.getCompletedSurveys(99L))
                .thenThrow(new MemberNotFoundException(99L));

        // when-then
        mockMvc.perform(get("/api/members/99/surveys/completed")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Member with id 99 not found")));
    }

    /**
     * Test case: Valid member ID, points exist.
     * Expected behavior: Returns 200 status and correct points.
     */
    @Test
    void testGetPoints_ValidMemberId_ReturnsPoints() throws Exception {
        // given
        when(memberService.getPoints(1L)).thenReturn(150);

        // when-then
        mockMvc.perform(get("/api/members/1/points")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(150)));
    }

    /**
     * Test case: Invalid member ID, member does not exist.
     * Expected behavior: Returns 404 status with error message.
     */
    @Test
    void testGetPoints_InvalidMemberId_ReturnsNotFound() throws Exception {
        // given
        when(memberService.getPoints(99L))
                .thenThrow(new MemberNotFoundException(99L));

        // when-then
        mockMvc.perform(get("/api/members/99/points")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Member with id 99 not found")));
    }
}