package hu.vszili.survey.service;

import hu.vszili.survey.data.Member;
import hu.vszili.survey.data.Participation;
import hu.vszili.survey.data.SurveyStatistic;
import hu.vszili.survey.exception.SurveyNotFoundException;
import hu.vszili.survey.repositroy.MemberRepository;
import hu.vszili.survey.repositroy.ParticipationRepository;
import hu.vszili.survey.repositroy.StatusRepository;
import hu.vszili.survey.repositroy.SurveyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyService {

    private final MemberRepository memberRepository;
    private final SurveyRepository surveyRepository;
    private final StatusRepository statusRepository;
    private final ParticipationRepository participationRepository;

    private static final String COMPLETED_STATUS = "Completed";
    private static final String FILTERED_STATUS = "Filtered";
    private static final String NOT_ASKED_STATUS = "Not asked";
    private static final String REJECTED_STATUS = "Rejected";

    /**
     * Retrieves a list of members who have completed a specific survey.
     * A survey is considered completed for a member if the member's participation status
     * matches the "Completed" status and is associated with the specified survey ID.
     *
     * @param surveyId the unique identifier of the survey for which completed members need to be retrieved
     * @return a list of {@link Member} objects representing the members who have completed the specified survey
     * @throws SurveyNotFoundException if no survey exists with the given survey ID
     */
    // TODO: unit test
    public List<Member> getMembersCompleted(Long surveyId) {
        if (!surveyRepository.existsById(surveyId)) {
            throw new SurveyNotFoundException(surveyId);
        }

        Long completedStatusId = statusRepository.findIdByName(COMPLETED_STATUS);
        return participationRepository.findAll().stream()
                .filter(participation ->
                    participation.getStatus().equals(completedStatusId) && participation.getSurveyId().equals(surveyId))
                .map(participation -> memberRepository.findById(participation.getMemberId()))
                .flatMap(Optional::stream)
                .toList();
    }

    /**
     * Retrieves a list of members who are eligible for a specific survey.
     * A member is considered eligible if they have not participated in the specified survey and their participation
     * status for the survey is "Not asked". Additionally, only active members are included in the result.
     *
     * @param surveyId the unique identifier of the survey for which eligible members need to be retrieved
     * @return a list of {@code Member} objects representing the active and eligible members for the specified survey
     */
    // TODO: unit test
    public List<Member> getEligibleMembers(Long surveyId) {
        Long notAskedStatusId = statusRepository.findIdByName(NOT_ASKED_STATUS);
        Map<Long, Member> members = memberRepository.getMembers();

        participationRepository.findAll()
                .forEach(participation -> {
                    if (participation.getSurveyId().equals(surveyId) && !participation.getStatus().equals(notAskedStatusId)) {
                        members.remove(participation.getMemberId());
                    }
                });

        return members.values().stream()
                .filter(Member::getActive)
                .toList();
    }

    /**
     * Retrieves statistics for all surveys by processing participation data.
     * Statistics include details about the number of completed, filtered,
     * and rejected participations, as well as the average length of participations
     * for each survey.
     *
     * @return a list of {@code SurveyStatistic} objects, where each object contains
     *         statistical data related to a specific survey
     */
    // TODO: unit test
    public List<SurveyStatistic> getStatistics() {
        Long completedStatusId = statusRepository.findIdByName(COMPLETED_STATUS);
        Long filteredStatusId = statusRepository.findIdByName(FILTERED_STATUS);
        Long rejectedStatusId = statusRepository.findIdByName(REJECTED_STATUS);

        List<SurveyStatistic> surveyStatistics = new ArrayList<>();

        Map<Long, List<Participation>> participationGrouping = participationRepository.findAll().stream()
                .collect(groupingBy(Participation::getSurveyId));

        participationGrouping.forEach((surveyID, participationList) ->
                surveyStatistics.add(
                    this.createStatistic(surveyID, participationList, completedStatusId, filteredStatusId, rejectedStatusId)));
        return surveyStatistics;
    }

    private SurveyStatistic createStatistic(Long surveyId, List<Participation> participation,
                                            Long completedId, Long filteredId, Long rejectedId) {

        Map<Long, Long> countsByStatus = participation.stream()
                .collect(groupingBy(
                        Participation::getStatus,
                        counting()
                ));

        double avgLength = participation.stream()
                .map(Participation::getLength)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        return SurveyStatistic.builder()
                .id(surveyId)
                .name(surveyRepository.getNameById(surveyId))
                .completedCount(countsByStatus.getOrDefault(completedId, 0L))
                .filteredCount(countsByStatus.getOrDefault(filteredId, 0L))
                .rejectedCount(countsByStatus.getOrDefault(rejectedId, 0L))
                .averageLength(avgLength)
                .build();
    }

}
