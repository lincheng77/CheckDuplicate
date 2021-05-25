package cn.edkso.checkduplicate.entry;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class HomeworkClazz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer homeworkId;
    private Integer clazzId;
    private String clazzName;

    private Integer submitted;

    private Integer total;

    private Integer state;

    @Transient
    private List<HomeworkStudent> homeworkStudentList;

    public HomeworkClazz() {
    }

    public HomeworkClazz(Integer homeworkId, Integer clazzId) {
        this.homeworkId = homeworkId;
        this.clazzId = clazzId;
    }
}
