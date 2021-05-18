package cn.edkso.checkduplicate.service;


import cn.edkso.checkduplicate.entry.Clazz;
import cn.edkso.checkduplicate.entry.Homework;
import cn.edkso.checkduplicate.entry.HomeworkStudent;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;

public interface HomeworkService {

    Homework save(String[] clazzIdArr, Homework homeWork);

    Page<HomeworkStudent> listPageForStudent(Integer page, Integer limit, Integer submitted, String deadline);

}
