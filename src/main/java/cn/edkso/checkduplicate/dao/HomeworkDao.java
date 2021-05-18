package cn.edkso.checkduplicate.dao;


import cn.edkso.checkduplicate.entry.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeworkDao extends JpaRepository<Homework, Integer> {

}
