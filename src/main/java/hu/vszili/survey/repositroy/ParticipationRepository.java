package hu.vszili.survey.repositroy;

import com.opencsv.bean.CsvToBeanBuilder;
import hu.vszili.survey.data.Participation;
import hu.vszili.survey.exception.CsvReadingException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Slf4j
@Repository
public class ParticipationRepository {

    @Value("${csv.base-path}")
    private String csvBasePath;

    private List<Participation> participation;

    @PostConstruct
    private void init() {
        participation = this.readParticipation();
        log.info("Participation loaded: {}", this.participation.size());
    }

    private List<Participation> readParticipation() {
        try (FileReader fileReader = new FileReader(csvBasePath + "Participation.csv")) {
            return new CsvToBeanBuilder<Participation>(fileReader).withType(Participation.class).build().parse();
        } catch (IOException | IllegalStateException e) {
            throw new CsvReadingException("Error reading participation file: " + e.getMessage());
        }
    }

    public List<Participation> findAll() {
        return participation;
    }

}
