package cn.edkso.checkduplicate.dao;

import cn.edkso.checkduplicate.entry.Clazz;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClazzDao extends JpaRepository<Clazz, Integer> {

    Page<Clazz> findByNameAndGradeAndCollegeAndCounselor(Pageable pageable, String name, Integer grade, String college, String counselor);

    List<Clazz> findByName(String name);

    List<Clazz> findAllByNameLike(String name);
}
