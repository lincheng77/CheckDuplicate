package cn.edkso.checkduplicate.dao;

import cn.edkso.checkduplicate.CheckduplicateApplication;
import cn.edkso.checkduplicate.entry.Student;
import cn.edkso.checkduplicate.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class StudentDaoTest {

    @Autowired private StudentDao studentDao;
    @Autowired private StudentService studentService;
    @Test
    public void findAllByClazzId(){
        List<Student> allByClazzId = studentDao.findAllByClazzId(3);
        System.out.println(allByClazzId);
        List<Student> allByClazzId2 = studentService.findAllByClazzId(3);
        System.out.println(allByClazzId2);
    }
}