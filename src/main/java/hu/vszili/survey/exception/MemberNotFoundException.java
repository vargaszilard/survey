package hu.vszili.survey.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(Long memberId) {
        super(String.format("Member with id %s not found", memberId));
    }
}
