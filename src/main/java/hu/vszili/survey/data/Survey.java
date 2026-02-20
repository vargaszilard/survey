package hu.vszili.survey.data;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Survey {

    @CsvBindByName(column = "Survey Id")
    private Long id;
    @CsvBindByName(column = "Name")
    private String name;
    @CsvBindByName(column = "Expected completes")
    private Integer expectedCompletes;
    @CsvBindByName(column = "Completion points")
    private Integer completionPoints;
    @CsvBindByName(column = "Filtered points")
    private Integer filteredPoint;

}
