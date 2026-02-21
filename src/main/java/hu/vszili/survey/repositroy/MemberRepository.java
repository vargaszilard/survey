package hu.vszili.survey.repositroy;

import hu.vszili.survey.data.Member;

import java.util.Map;
import java.util.Optional;

public interface MemberRepository {

    Optional<Member> findById(Long memberId);

    boolean existsById(Long surveyId);

    Map<Long, Member> getMembers();

}
