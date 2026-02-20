package hu.vszili.survey.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SurveyStatistic {

    private Long id;
    private String name;
    private Long completedCount;
    private Long filteredCount;
    private Long rejectedCount;
    private Double averageLength;

}
