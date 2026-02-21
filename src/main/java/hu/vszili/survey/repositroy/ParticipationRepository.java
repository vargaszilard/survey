package hu.vszili.survey.repositroy;

import hu.vszili.survey.data.Participation;

import java.util.List;

public interface ParticipationRepository {

    List<Participation> findAll();

}
