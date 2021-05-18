package cn.edkso.checkduplicate.service.impl;

import cn.edkso.checkduplicate.dao.StudentDao;
import cn.edkso.checkduplicate.entry.Student;
import cn.edkso.checkduplicate.service.StudentService;
import cn.edkso.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentDao studentDao;

    @Override
    public Student login(String username, String password) {
        Student student = studentDao.findByUsernameAndPassword(username, MD5Utils.md5(password));
        if(student != null){
            return student;
        }
        return null;
    }

    @Override
    public Student register(String username, String password, String name) {
        Student student = new Student();
        student.setState(1);
        student.setName(name);
        student.setUsername(username);
        student.setPassword(MD5Utils.md5(password));
        student.setName(name);
        Student resStudent = studentDao.save(student);
        return resStudent;
    }

    @Override
    public List<Student> findAllByClazzId(Integer id) {
        return studentDao.findAllByClazzId(id);
    }


}
