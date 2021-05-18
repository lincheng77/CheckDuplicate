package cn.edkso.checkduplicate.dao;


import cn.edkso.checkduplicate.entry.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectDao extends JpaRepository<Subject, Integer> {

    List<Subject> findByName(String name);

    List<Subject> findByNameLike(String name);
}
