package cn.edkso.checkduplicate.service;

import cn.edkso.checkduplicate.entry.Teacher;

public interface TeacherService {
    public Teacher login(String usernmae, String password);
}
