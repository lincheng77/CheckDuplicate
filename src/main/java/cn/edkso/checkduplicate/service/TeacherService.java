package cn.edkso.checkduplicate.service;

import cn.edkso.checkduplicate.entry.Teacher;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TeacherService {
    public Teacher login(String usernmae, String password);

    Page<Teacher> listByPage(Integer page, Integer limit, Teacher teacher);

    Teacher updateForManager(Teacher teacher);

    void delForManager(List<Teacher> teacherList);
}
