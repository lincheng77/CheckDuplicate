package cn.edkso.checkduplicate.dao;


import cn.edkso.checkduplicate.entry.Homework;
import cn.edkso.checkduplicate.entry.HomeworkStudent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeworkDao extends JpaRepository<Homework, Integer>, JpaSpecificationExecutor<Homework> {

    Page<Homework> findAll(Specification specification, Pageable pageable);
}
