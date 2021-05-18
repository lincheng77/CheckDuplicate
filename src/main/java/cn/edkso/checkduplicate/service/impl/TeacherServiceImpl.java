package cn.edkso.checkduplicate.service.impl;

import cn.edkso.checkduplicate.dao.TeacherDao;
import cn.edkso.checkduplicate.entry.Student;
import cn.edkso.checkduplicate.entry.Teacher;
import cn.edkso.checkduplicate.service.TeacherService;
import cn.edkso.utils.MD5Utils;
import cn.edkso.utils.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

@Service
@Transactional
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherDao teacherDao;

    @Override
    public Teacher login(String username, String password) {

        //3. 查库登录
        Teacher teacher = teacherDao.findByUsernameAndPassword(username, MD5Utils.md5(password));
        if(teacher != null){
            return teacher;
        }
        return null;
    }
}
