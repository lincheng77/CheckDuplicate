package cn.edkso.checkduplicate.dao;


import cn.edkso.checkduplicate.entry.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerDao extends JpaRepository<Manager, Integer> {

    Manager findByUsernameAndPassword(String username, String md5);
}
