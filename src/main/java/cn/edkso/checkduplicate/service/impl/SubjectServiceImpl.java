package cn.edkso.checkduplicate.service.impl;

import cn.edkso.checkduplicate.constant.ExceptionDefault;
import cn.edkso.checkduplicate.dao.SubjectDao;
import cn.edkso.checkduplicate.entry.Subject;
import cn.edkso.checkduplicate.exception.CDException;
import cn.edkso.checkduplicate.service.SubjectService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectDao subjectDao;

    @Override
    public List<Subject> listAll() {
        return subjectDao.findAll();
    }

    @Override
    public void del(List<Subject> subjectList) {
        try {
            subjectDao.deleteAll(subjectList);
        } catch (Exception e){
            throw new CDException(ExceptionDefault.DEL_ERROR);
        }
    }

    @Override
    public Subject saveAndUpdate(Subject subject) {
        if (subject.getId() != null){
            //1. 修改
            //1.1 查询记录
            Optional<Subject> subjectOptional = subjectDao.findById(subject.getId());
            Subject resSubject = subjectOptional.get();

            //1.2 对记录修改
            if (StringUtils.isNotBlank(subject.getName())){
                resSubject.setName(subject.getName());
            }
            if (StringUtils.isNotBlank(subject.getIntroduction())){
                resSubject.setIntroduction(subject.getIntroduction());
            }
            if (subject.getState() != null){
                resSubject.setState(subject.getState());
            }
            return subjectDao.save(resSubject);

        }else{
            //2. 增加
            //2.1 查重是否已经存在
            List<Subject> list = subjectDao.findByName(subject.getName());
            if (list.size() == 0){
                //2.1.1 插入
                subject.setState(subject.getState() == null ? 0:1);
                return subjectDao.save(subject);
            }else{
                //2.1.1 存在
                throw new CDException(ExceptionDefault.RECORD_EXIST);
            }
        }
    }

    @Override
    public Page<Subject> listByPage(Integer page, Integer limit, Subject subject) {
        Pageable pageable = PageRequest.of(page -1,limit);


        Specification specification = (root, cq, cb) -> {
            Predicate predicate = cb.conjunction();
            //增加筛选条件0(学科名称模糊匹配)
            if (StringUtils.isNotBlank(subject.getName())){
                predicate.getExpressions().add(cb.like(root.get("name"), "%" + subject.getName() + "%"));
            }


            return predicate;
        };

        Page<Subject> subjectPage = subjectDao.findAll(specification, pageable);

        return subjectPage;
    }

    @Override
    public List<Subject> listByNameLike(String name) {

        return subjectDao.findByNameLike(name);
    }

    @Override
    public Subject findById(Integer subjectId) {
        return subjectDao.findById(subjectId).get();
    }
}
