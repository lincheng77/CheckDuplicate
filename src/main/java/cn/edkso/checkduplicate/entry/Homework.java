package cn.edkso.checkduplicate.entry;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;


@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Homework {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String content;

    private String filePath;
    private String fileName;
    private String fileRandomName;

    private Integer subjectId;
    private Integer teacherId;
    private String subjectName;

    private Integer state;

    private Integer submitted;
    private Integer total;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp deadline;

    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createTime;

    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp updateTime;

    @Transient
    private List<HomeworkClazz> homeworkClazzList;

}
