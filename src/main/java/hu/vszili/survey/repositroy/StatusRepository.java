package hu.vszili.survey.repositroy;

public interface StatusRepository {

    Long findIdByName(String completedStatus);

}
