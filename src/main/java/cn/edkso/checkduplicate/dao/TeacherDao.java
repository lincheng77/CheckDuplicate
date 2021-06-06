package cn.edkso.checkduplicate.dao;

import cn.edkso.checkduplicate.entry.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherDao extends JpaRepository<Teacher, Integer>, JpaSpecificationExecutor<Teacher> {
    public Teacher findByUsername(String username);

    public Teacher findByUsernameAndPassword(String username, String md5);
}
