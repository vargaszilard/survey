package hu.vszili.survey.data;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class Participation {

    @CsvBindByName(column = "Member Id")
    private Long memberId;
    @CsvBindByName(column = "Survey Id")
    private Long surveyId;
    @CsvBindByName(column = "Status")
    private Long status;
    @CsvBindByName(column = "Length")
    private Integer length;

}
