package hu.vszili.survey.service;

import hu.vszili.survey.data.Survey;
import hu.vszili.survey.exception.MemberNotFoundException;
import hu.vszili.survey.repositroy.MemberRepository;
import hu.vszili.survey.repositroy.ParticipationRepository;
import hu.vszili.survey.repositroy.StatusRepository;
import hu.vszili.survey.repositroy.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final StatusRepository statusRepository;
    private final SurveyRepository surveyRepository;
    private final ParticipationRepository participationRepository;

    private static final String COMPLETED_STATUS = "Completed";
    private static final String FILTERED_STATUS = "Filtered";

    /**
     * Retrieves a list of completed surveys for a specific member.
     * A survey is considered completed if its status matches the "Completed" status
     * and it is associated with the specified member ID.
     *
     * @param memberId the unique identifier of the member whose completed surveys need to be retrieved
     * @return a list of {@link Survey} objects representing the completed surveys for the specified member
     * @throws MemberNotFoundException if no member exists with the given member ID
     */
    public List<Survey> getCompletedSurveys(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException(memberId);
        }

        Long completedStatusId = statusRepository.findIdByName(COMPLETED_STATUS);
        return participationRepository.findAll().stream()
                .filter(participation ->
                        participation.getStatus().equals(completedStatusId) && participation.getMemberId().equals(memberId))
                .map(participation -> surveyRepository.findById(participation.getSurveyId()))
                .flatMap(Optional::stream)
                .toList();
    }

    /**
     * Calculates the total points earned by a member across completed or filtered surveys.
     * <p>
     * The method retrieves and filters participations for the specified member ID
     * that have a status of either "Completed" or "Filtered". It calculates the points
     * based on the survey's status and sums them up.
     *
     * @param memberId the unique identifier of the member whose points need to be calculated
     * @return the total points earned by the member based on completed or filtered surveys
     * @throws MemberNotFoundException if no member exists with the given member ID
     */
    public Integer getPoints(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException(memberId);
        }
        Long completedStatusId = statusRepository.findIdByName(COMPLETED_STATUS);
        Long filteredStatusId = statusRepository.findIdByName(FILTERED_STATUS);

        return participationRepository.findAll().stream()
                .filter(p ->
                        p.getMemberId().equals(memberId) &&
                                (p.getStatus().equals(completedStatusId) || p.getStatus().equals(filteredStatusId)))
                .mapToInt(participation -> {
                    boolean isCompleted = participation.getStatus().equals(completedStatusId);
                    return surveyRepository.getPoints(participation.getSurveyId(), isCompleted);
                })
                .sum();
    }

}
