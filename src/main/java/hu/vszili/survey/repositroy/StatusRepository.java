package hu.vszili.survey.repositroy;

import com.opencsv.bean.CsvToBeanBuilder;
import hu.vszili.survey.data.Status;
import hu.vszili.survey.exception.CsvReadingException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class StatusRepository {

    @Value("${csv.base-path}")
    private String csvBasePath;

    private final Map<String, Status> statuses = new ConcurrentHashMap<>();

    @PostConstruct
    private void init() {
        List<Status> statusList = this.readStatuses();
        this.statuses.putAll(
                statusList.stream().collect(
                        Collectors.toMap(Status::getName, Function.identity())));
        log.info("Statuses loaded: {}", statuses.size());
    }

    private List<Status> readStatuses() {
        try (FileReader fileReader = new FileReader(csvBasePath + "Statuses.csv")) {
            return new CsvToBeanBuilder<Status>(fileReader).withType(Status.class).build().parse();
        } catch (IOException | IllegalStateException e) {
            throw new CsvReadingException("Error reading statuses file: " + e.getMessage());
        }
    }


    public Long findIdByName(String completedStatus) {
        return statuses.get(completedStatus).getId();
    }
}
