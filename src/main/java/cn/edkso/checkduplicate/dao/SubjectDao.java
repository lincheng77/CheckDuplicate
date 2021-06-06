package cn.edkso.checkduplicate.dao;


import cn.edkso.checkduplicate.entry.Clazz;
import cn.edkso.checkduplicate.entry.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SubjectDao extends JpaRepository<Subject, Integer>, JpaSpecificationExecutor<Subject> {

    List<Subject> findByName(String name);

    List<Subject> findByNameLike(String name);
}
