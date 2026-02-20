package hu.vszili.survey.data;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class Status {

    @CsvBindByName(column = "Status Id")
    private Long id;
    @CsvBindByName(column = "Name")
    private String name;

}
