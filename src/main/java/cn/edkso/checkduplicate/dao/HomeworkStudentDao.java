package cn.edkso.checkduplicate.dao;

import cn.edkso.checkduplicate.entry.HomeworkStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeworkStudentDao extends JpaRepository<HomeworkStudent, Integer>, JpaSpecificationExecutor<HomeworkStudent> {


}
