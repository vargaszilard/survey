package hu.vszili.survey.repositroy.impl;

import com.opencsv.bean.CsvToBeanBuilder;
import hu.vszili.survey.data.Survey;
import hu.vszili.survey.exception.CsvReadingException;
import hu.vszili.survey.repositroy.SurveyRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class SurveyRepositoryImpl implements SurveyRepository {

    @Value("${csv.base-path}")
    private String csvBasePath;

    private final Map<Long, Survey> surveys = new ConcurrentHashMap<>();

    @PostConstruct
    private void init() {
        List<Survey> memberList = this.readSurveys();
        this.surveys.putAll(
                memberList.stream().collect(
                        Collectors.toMap(Survey::getId, Function.identity())));
        log.info("Members loaded: {}", surveys.size());
    }

    private List<Survey> readSurveys() {
        try (FileReader fileReader = new FileReader(csvBasePath + "Surveys.csv")) {
            return new CsvToBeanBuilder<Survey>(fileReader).withType(Survey.class).build().parse();
        } catch (IOException | IllegalStateException e) {
            throw new CsvReadingException("Error reading surveys file: " + e.getMessage());
        }
    }

    @Override
    public boolean existsById(Long surveyId) {
        return surveys.containsKey(surveyId);
    }

    @Override
    public Optional<Survey> findById(Long surveyId) {
        return  Optional.of(surveys.get(surveyId));
    }

    @Override
    public Integer getPoints(Long surveyId, Boolean isComplete) {
        Survey survey = surveys.get(surveyId);
        return Boolean.TRUE.equals(isComplete) ? survey.getCompletionPoints() :  survey.getFilteredPoint();
    }

    @Override
    public String getNameById(Long surveyId) {
        return surveys.get(surveyId).getName();
    }

}
