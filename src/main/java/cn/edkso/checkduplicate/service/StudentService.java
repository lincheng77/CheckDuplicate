package cn.edkso.checkduplicate.service;


import cn.edkso.checkduplicate.entry.Student;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StudentService {
    public Student login(String username, String password);


    Student register(String username, String password, String name);

    List<Student> findAllByClazzId(Integer id);

    Student update(Student oldStudent);

    Page<Student> listByPage(Integer page, Integer limit, Student student);

    Student updateForManager(Student student);

    void delForManager(List<Student> studentList);
}
