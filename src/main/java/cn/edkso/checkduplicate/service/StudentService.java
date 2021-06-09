package cn.edkso.checkduplicate.service;


import cn.edkso.checkduplicate.entry.Student;

import java.util.ArrayList;
import java.util.List;

public interface StudentService {
    public Student login(String username, String password);


    Student register(String username, String password, String name);

    List<Student> findAllByClazzId(Integer id);

    Student update(Student oldStudent);

    List<Student> addListByImport(List<ArrayList<String>> excelList, Integer clazzId);

}
