package cn.edkso.checkduplicate.dao;

import cn.edkso.checkduplicate.entry.Clazz;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ClazzDaoTest {

    @Autowired
    private ClazzDao clazzDao;
    @Test
    void findByName() {

        List<Clazz> byName = clazzDao.findByName("软件工程17-01");
        System.out.println(byName);
    }

    @Test
    void findAllByNameLike() {
    }
}