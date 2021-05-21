package cn.edkso.checkduplicate.dao;


import cn.edkso.checkduplicate.entry.HomeworkClazz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HomeworkClazzDao extends JpaRepository<HomeworkClazz, Integer> {
    Optional<HomeworkClazz> findByHomeworkIdAndClazzId(Integer homeworkId, Integer clazzId);
}
