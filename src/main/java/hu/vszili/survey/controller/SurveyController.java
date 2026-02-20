package hu.vszili.survey.controller;

import hu.vszili.survey.data.Member;
import hu.vszili.survey.data.SurveyStatistic;
import hu.vszili.survey.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/surveys")
public class SurveyController {

    private final SurveyService surveyService;

    /**
     * Retrieves a list of members who have completed the specified survey.
     *
     * @param surveyId the unique identifier of the survey for which completed members are to be retrieved
     * @return a list of {@code Member} objects representing the members who have completed the specified survey
     * @throws SurveyNotFoundException if no survey exists with the provided {@code surveyId}
     */
    @GetMapping("/{surveyId}/members/completed")
    public List<Member> getCompletedMembers(@PathVariable Long surveyId) {
        return surveyService.getMembersCompleted(surveyId);
    }

    /**
     * Retrieves a list of eligible members for a specific survey. A member is considered eligible if
     * they have not participated in the given survey and their status for the survey is "Not asked".
     * Only active members are included in the resulting list.
     *
     * @param surveyId the unique identifier of the survey for which eligible members are to be retrieved
     * @return a list of {@code Member} objects representing the active members eligible for the specified survey
     */
    @GetMapping("/{surveyId}/eligible-members")
    public List<Member> getEligibleMembers(@PathVariable Long surveyId) {
        return surveyService.getEligibleMembers(surveyId);
    }

    /**
     * Retrieves statistical information about all surveys. The statistics include details such as the number
     * of completed, filtered, and rejected participations for each survey, as well as the average length
     * of participations.
     *
     * @return a list of {@code SurveyStatistic} objects where each object contains statistical data for a survey
     */
    // TODO: unit test
    @GetMapping("/statistics")
    public List<SurveyStatistic> getSurveyStatistics() {
        return surveyService.getStatistics();
    }

}
