package cn.edkso.checkduplicate.service.impl;

import cn.edkso.checkduplicate.constant.ExceptionDefault;
import cn.edkso.checkduplicate.dao.ClazzDao;
import cn.edkso.checkduplicate.entry.Clazz;
import cn.edkso.checkduplicate.exception.CDException;
import cn.edkso.checkduplicate.service.ClazzService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClazzServiceImpl implements ClazzService {

    @Autowired
    private ClazzDao clazzDao;

    @Override
    public List<Clazz> listAll() {
        return clazzDao.findAll();
    }

    @Override
    public Page<Clazz> listByPage(Integer page, Integer limit, Clazz clazz) {
        //切记 datajpa从0页开始

        Pageable pageable = PageRequest.of(page -1,limit);
        Specification specification = (root, cq, cb) -> {
            Predicate predicate = cb.conjunction();
            //增加筛选条件0(班级名称模糊匹配)
            if (StringUtils.isNotBlank(clazz.getName())){
                predicate.getExpressions().add(cb.like(root.get("name"), "%" + clazz.getName() + "%"));
            }
            //增加筛选条件1(辅导员模糊匹配)
            if (StringUtils.isNotBlank(clazz.getCounselor())){
                predicate.getExpressions().add(cb.like(root.get("counselor"), "%" + clazz.getCounselor() + "%"));
            }
            //增加筛选条件2(学院模糊匹配)
            if (StringUtils.isNotBlank(clazz.getCollege())){
                predicate.getExpressions().add(cb.like(root.get("college"), "%" + clazz.getCollege() + "%"));
            }

            //增加筛选条件3(年级模糊匹配)
            if ((clazz.getGrade() != null)){
                predicate.getExpressions().add(cb.equal(root.get("grade"), clazz.getGrade()));
            }

            return predicate;
        };

        Page<Clazz> clazzPage = clazzDao.findAll(specification, pageable);
        return clazzPage;
    }

    @Override
    public List<Clazz> listByNameLike(String name) {
        return clazzDao.findAllByNameLike(name);
    }

    @Override
    public Clazz findById(Integer clazzId) {
        return clazzDao.findById(clazzId).get();
    }

    @Override
    public void del(List<Clazz> clazzList) {
        try {
            clazzDao.deleteAll(clazzList);
        } catch (Exception e){
            throw new CDException(ExceptionDefault.DEL_ERROR);
        }
    }

    @Override
    public Clazz saveAndUpdate(Clazz clazz) {
        if (clazz.getId() != null){
            //1. 修改
            //1.1 查询记录
            Optional<Clazz> clazzOptional = clazzDao.findById(clazz.getId());
            Clazz resClazz = clazzOptional.get();

            //1.2 对记录修改
            if (StringUtils.isNotBlank(clazz.getCollege())){
                resClazz.setCollege(clazz.getCollege());
            }
            if (StringUtils.isNotBlank(clazz.getCounselor())){
                resClazz.setCounselor(clazz.getCounselor());
            }
            if (StringUtils.isNotBlank(clazz.getName())){
                resClazz.setName(clazz.getName());
            }
            if (clazz.getState() != null){
                resClazz.setState(clazz.getState());
            }
            if (clazz.getGrade() != null){
                resClazz.setGrade(clazz.getGrade());
            }
            if (clazz.getNumbers() != null){
                resClazz.setNumbers(clazz.getNumbers());
            }
            return clazzDao.save(resClazz);

        }else{
            //2. 增加
            //2.1 查重是否已经存在
            List<Clazz> list = clazzDao.findByName(clazz.getName());
            if (list.size() == 0){
                //2.1.1 插入
                clazz.setState(clazz.getState() == null ? 0:1);
                return clazzDao.save(clazz);
            }else{
                //2.1.1 存在
                throw new CDException(ExceptionDefault.RECORD_EXIST);
            }
        }
    }

}
