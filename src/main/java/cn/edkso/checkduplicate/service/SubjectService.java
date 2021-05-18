package cn.edkso.checkduplicate.service;

import cn.edkso.checkduplicate.entry.Clazz;
import cn.edkso.checkduplicate.entry.Subject;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SubjectService {

    public List<Subject> listAll();

    void del(List<Subject> subjectList);

    Subject saveAndUpdate(Subject subject);

    Page<Subject> listByPage(Integer page, Integer limit, Subject subject);

    List<Subject> listByNameLike(String name);
}
