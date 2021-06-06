package cn.edkso.checkduplicate.service.impl;

import cn.edkso.checkduplicate.dao.ManagerDao;
import cn.edkso.checkduplicate.entry.Manager;
import cn.edkso.checkduplicate.service.ManagerService;
import cn.edkso.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private ManagerDao managerDao;


    @Override
    public Manager login(String username, String password) {
        //3. 查库登录
        Manager manager = managerDao.findByUsernameAndPassword(username, MD5Utils.md5(password));
        if(manager != null){
            return manager;
        }
        return null;
    }

    @Override
    public Manager update(Manager oldManager) {
        return managerDao.save(oldManager);
    }
}
