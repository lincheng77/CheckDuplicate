package cn.edkso.checkduplicate.service.impl;

import cn.edkso.checkduplicate.dao.StudentDao;
import cn.edkso.checkduplicate.entry.Clazz;
import cn.edkso.checkduplicate.entry.Student;
import cn.edkso.checkduplicate.service.ClazzService;
import cn.edkso.checkduplicate.service.StudentService;
import cn.edkso.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentDao studentDao;
    @Autowired
    private ClazzService clazzService;

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

    @Override
    public Student update(Student oldStudent) {
        return studentDao.save(oldStudent);
    }

    @Override
    public List<Student> addListByImport(List<ArrayList<String>> excelList, Integer clazzId) {

        //1. 查询clazz
        Clazz clazz = clazzService.findById(clazzId);

        //2. 组装studentList
        List<Student> studentList = new ArrayList<>();
        for (int i = 1; i < excelList.size(); i++) {
            Student student = new Student();
            student.setUsername(excelList.get(i).get(0));
            student.setName(excelList.get(i).get(1));
            student.setPassword(MD5Utils.md5("123456"));
            student.setClazzId(clazzId);
            student.setClazzName(clazz.getName());
            student.setState(1);

            studentList.add(student);
        }

        List<Student> res = studentDao.saveAll(studentList);
        if (res != null){
            return res;
        }

        return null;
    }


}
