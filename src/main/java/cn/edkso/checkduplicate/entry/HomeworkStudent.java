package cn.edkso.checkduplicate.entry;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Entity
public class HomeworkStudent implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String homeworkName;
    private Integer homeworkId;
    private String subjectName;
    private String homeworkContent;
    private Integer clazzId;
    private Integer studentId;
    private String clazzName;
    private String studentName;


    private Integer state;

    private Integer submitted;
    private Integer isCheck;

    private Float codeRepeat;
    private Float textRepeat;
    private Float imgRepeat;

    private String filePath;
    private String fileName;
    private String fileRandomName;

    private String studentFilePath;
    private String studentFileName;
    private String studentFileRandomName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp deadline;

}
