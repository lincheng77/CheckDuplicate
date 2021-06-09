package cn.edkso.checkduplicate.service.impl;

import cn.edkso.checkduplicate.constant.ExceptionDefault;
import cn.edkso.checkduplicate.dao.TeacherDao;
import cn.edkso.checkduplicate.entry.Teacher;
import cn.edkso.checkduplicate.exception.CDException;
import cn.edkso.checkduplicate.service.TeacherService;
import cn.edkso.utils.MD5Utils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.List;

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

    @Override
    public Page<Teacher> listByPage(Integer page, Integer limit, Teacher teacher) {

        Pageable pageable = PageRequest.of(page -1,limit);

        Specification specification = (root, cq, cb) -> {
            Predicate predicate = cb.conjunction();
            //增加筛选条件0(教师名称模糊匹配)
            if (StringUtils.isNotBlank(teacher.getName())){
                predicate.getExpressions().add(cb.like(root.get("name"), "%" + teacher.getName() + "%"));
            }
            return predicate;
        };

        Page<Teacher> teacherPage = teacherDao.findAll(specification, pageable);
        return teacherPage;
    }

    @Override
    public Teacher updateForManager(Teacher teacher) {
        Teacher oldTeacher = teacherDao.findById(teacher.getId()).get();

        if (StringUtils.isNotBlank(teacher.getName())){
            oldTeacher.setName(teacher.getName());
        }
        if (StringUtils.isNotBlank(teacher.getPassword())){
            oldTeacher.setPassword(MD5Utils.md5(teacher.getPassword()));
        }
        if (teacher.getState() != null){
            oldTeacher.setState(teacher.getState());
        }
        return teacherDao.save(oldTeacher);
    }

    @Override
    public void delForManager(List<Teacher> teacherList) {
        try {
            teacherDao.deleteAll(teacherList);
        } catch (Exception e){
            throw new CDException(ExceptionDefault.DEL_ERROR);
        }
    }

    @Override
    public Teacher register(String username, String password, String name) {
        Teacher teacher = new Teacher();
        teacher.setState(1);
        teacher.setName(name);
        teacher.setUsername(username);
        teacher.setPassword(MD5Utils.md5(password));
        teacher.setName(name);
        Teacher resTeacher = teacherDao.save(teacher);
        return resTeacher;
    }
}
