package cn.edkso.checkduplicate.dao;

import cn.edkso.checkduplicate.entry.HomeworkStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface HomeworkStudentDao extends JpaRepository<HomeworkStudent, Integer>, JpaSpecificationExecutor<HomeworkStudent> {


    List<HomeworkStudent> findAllByHomeworkId(Integer id);

    List<HomeworkStudent> findAllByHomeworkIdAndClazzId(Integer homeworkId, Integer clazzId);
}
