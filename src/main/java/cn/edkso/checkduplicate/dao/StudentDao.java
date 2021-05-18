package cn.edkso.checkduplicate.dao;

import cn.edkso.checkduplicate.entry.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentDao extends JpaRepository<Student, Integer> {

    public Student findByUsername(String username);

    List<Student> findAllByClazzId(Integer clazzId);

    public Student findByUsernameAndPassword(String username, String password);
}
