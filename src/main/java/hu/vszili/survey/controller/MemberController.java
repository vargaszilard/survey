package hu.vszili.survey.controller;

import hu.vszili.survey.data.Survey;
import hu.vszili.survey.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    /**
     * Retrieves the list of surveys that have been completed by a specific member.
     *
     * @param memberId the unique identifier of the member whose completed surveys are to be retrieved
     * @return a list of {@code Survey} objects representing the surveys completed by the specified member
     * @throws MemberNotFoundException if no member exists with the provided {@code memberId}
     */
    @GetMapping("/{memberId}/surveys/completed")
    public List<Survey> getCompletedSurveys(@PathVariable Long memberId) {
        return memberService.getCompletedSurveys(memberId);
    }

    /**
     * Retrieves the total points accumulated by a specific member based on completed and filtered surveys.
     *
     * @param memberId the unique identifier of the member whose points are to be retrieved
     * @return the total points earned by the member for completed and filtered surveys
     * @throws MemberNotFoundException if no member exists with the provided {@code memberId}
     */
    @GetMapping("/{memberId}/points")
    public Integer getPoints(@PathVariable Long memberId) {
        return memberService.getPoints(memberId);
    }

}
