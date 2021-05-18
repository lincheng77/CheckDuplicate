package cn.edkso.checkduplicate.service.impl;


import cn.edkso.checkduplicate.dao.HomeworkClazzDao;

import cn.edkso.checkduplicate.dao.HomeworkDao;
import cn.edkso.checkduplicate.dao.HomeworkStudentDao;
import cn.edkso.checkduplicate.entry.*;
import cn.edkso.checkduplicate.exception.CDException;
import cn.edkso.checkduplicate.service.ClazzService;
import cn.edkso.checkduplicate.service.HomeworkService;
import cn.edkso.checkduplicate.service.StudentService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class HomeworkServiceImpl implements HomeworkService {

    @Autowired
    private HdfsService hdfsService;
    @Autowired
    private HomeworkDao homeworkDao;
    @Autowired
    private HomeworkStudentDao homeworkStudentDao;
    @Autowired
    private HomeworkClazzDao homeworkClazzDao;

    @Autowired
    private StudentService studentService;

    @Override
    public Homework save(String[] clazzIdArr,  Homework homeWork) {
        //1. 保存作业
        homeWork.setSubmitted(0);
        homeWork.setTotal(0);
        Homework homeworkRes = homeworkDao.save(homeWork);

        //2. 把hdfs tmp文件转移到相应的文件路径
        String oldPath = homeWork.getFilePath();
        String newPath = "/checkduplicate/homework/college/subject/" + homeworkRes.getId();
        try {
            if(!hdfsService.renameFile(oldPath , newPath)){
                throw new CDException("文件系统发生异常，请尝试重新提交");
            }
        } catch (Exception e) {
            throw new CDException("文件系统发生异常，请尝试重新提交");
        }

        //3. 修改数据库文件路径
        homeWork.setFilePath(newPath + oldPath.substring(oldPath.lastIndexOf("/") ,oldPath.length()));
        homeworkRes = homeworkDao.save(homeWork);


        List<HomeworkStudent> homeworkStudentList = new ArrayList<>();

        //4. 保存作业-班级List
        List<HomeworkClazz> homeworkClazzList = new ArrayList<>();
        for (String clazzId : clazzIdArr) {
            HomeworkClazz homeworkClazz = new HomeworkClazz();
            homeworkClazz.setHomeworkId(homeworkRes.getId());
            homeworkClazz.setClazzId(Integer.valueOf(clazzId));
            homeworkClazz.setSubmitted(0);
            homeworkClazz.setTotal(0);
            homeworkClazz.setState(1);
            homeworkClazzList.add(homeworkClazz);

            List<Student> studentList = studentService.findAllByClazzId(homeworkClazz.getClazzId());
            for (Student student : studentList) {
                HomeworkStudent homeworkStudent = new HomeworkStudent();
                homeworkStudent.setHomeworkId(homeworkRes.getId());
                homeworkStudent.setStudentId(student.getId());
                homeworkStudent.setClazzId(homeworkClazz.getClazzId());
                homeworkStudent.setState(1); //可用
                homeworkStudent.setSubmitted(0); //未提交
                homeworkStudent.setRepeatRate(0.0f); //重复率
                homeworkStudent.setIsCheck(0); //未查重
                homeworkStudentList.add(homeworkStudent);
            }
        }
        homeworkClazzDao.saveAll(homeworkClazzList);

        //5. 保存作业-学生List
        homeworkStudentDao.saveAll(homeworkStudentList);

        return homeworkRes;
    }

    @Override
    public Page<HomeworkStudent> listPageForStudent(Integer page, Integer limit, Integer submitted, String deadline) {

        Pageable pageable = PageRequest.of(page -1,limit);
        HomeworkStudent homeworkStudent = new HomeworkStudent();

        if(StringUtils.isNotBlank(deadline)){
            homeworkStudent.setDeadline(Timestamp.valueOf(deadline));
        }
        if (submitted != null){
            homeworkStudent.setSubmitted(submitted);
        }

        Example<HomeworkStudent> example = Example.of(homeworkStudent);

        Page<HomeworkStudent> homeworkStudentPage = homeworkStudentDao.findAll(example, pageable);
        return homeworkStudentPage;
    }
}
