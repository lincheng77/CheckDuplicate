package cn.edkso.checkduplicate.dao;

import cn.edkso.checkduplicate.entry.HomeworkClazz;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HomeworkClazzDaoTest {

    @Autowired
    private HomeworkClazzDao homeworkClazzDao;

    @Test
    public void findAllByHomeworkIdTest(){
        List<HomeworkClazz> allByHomeworkId = homeworkClazzDao.findAllByHomeworkId(10);
        System.out.println(allByHomeworkId);
    }

}