package cn.edkso.checkduplicate.service;


import cn.edkso.checkduplicate.entry.Manager;

public interface ManagerService {
    Manager login(String username, String password);

}
