package cn.edkso.checkduplicate.service;

import cn.edkso.checkduplicate.entry.Clazz;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ClazzService {


    public List<Clazz> listAll();

    void del(List<Clazz> clazzList);

    Clazz saveAndUpdate(Clazz clazz);

    Page<Clazz> listByPage(Integer page, Integer limit, Clazz clazz);
}
