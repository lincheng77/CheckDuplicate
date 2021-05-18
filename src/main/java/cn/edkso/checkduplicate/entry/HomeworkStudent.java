package cn.edkso.checkduplicate.entry;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@Data
@Entity
public class HomeworkStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String homeworkName;
    private Integer homeworkId;
    private String subjectName;
    private String homeworkContent;
    private Integer clazzId;
    private Integer studentId;

    private Integer state;

    private Integer submitted;
    private Integer isCheck;

    private Float codeRepeat;
    private Float textRepeat;
    private Float imgRepeat;

    private String filePath;

    private Timestamp deadline;
}
