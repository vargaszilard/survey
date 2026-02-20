package hu.vszili.survey.data;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class Member {

    @CsvBindByName(column = "Member Id")
    private Long id;
    @CsvBindByName(column = "Full name")
    private String fullName;
    @CsvBindByName(column = "E-mail address")
    private String email;
    @CsvBindByName(column = "Is Active")
    private Boolean active;

}
