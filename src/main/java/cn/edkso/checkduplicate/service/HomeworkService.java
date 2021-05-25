package cn.edkso.checkduplicate.service;


import cn.edkso.checkduplicate.entry.Clazz;
import cn.edkso.checkduplicate.entry.Homework;
import cn.edkso.checkduplicate.entry.HomeworkStudent;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;
import java.util.List;

public interface HomeworkService {

    @Deprecated
    Homework save(String[] clazzIdArr, Homework homeWork);

    Page<HomeworkStudent> listPageForStudent(Integer page, Integer limit, Integer submitted,
                                             String startTime, String deadline);

    HomeworkStudent submitHomework(HomeworkStudent homeworkStudent, String tmpPath);

    @Deprecated
    Page<Homework> listByPage(Integer page, Integer limit, Homework homework, String startTime);


    Page<Homework> listByPage(Integer page, Integer limit, String name, String subjectName, String startTime, String deadline);

    Homework save(String[] clazzIdArr, Homework homework, String tmpPath);

    void del(List<Homework> homeworkList);

}
