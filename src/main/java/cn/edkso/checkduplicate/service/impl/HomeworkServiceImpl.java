package cn.edkso.checkduplicate.service.impl;



import cn.edkso.checkduplicate.constant.ExceptionDefault;
import cn.edkso.checkduplicate.dao.HomeworkClazzDao;
import cn.edkso.checkduplicate.dao.HomeworkDao;
import cn.edkso.checkduplicate.dao.HomeworkStudentDao;
import cn.edkso.checkduplicate.entry.*;
import cn.edkso.checkduplicate.exception.CDException;
import cn.edkso.checkduplicate.service.ClazzService;
import cn.edkso.checkduplicate.service.HomeworkService;
import cn.edkso.checkduplicate.service.StudentService;
import cn.edkso.checkduplicate.service.SubjectService;
import cn.edkso.utils.CheckUtil;
import cn.edkso.utils.FileUtils;
import cn.textcheck.CheckManager;
import cn.textcheck.engine.checker.CheckTask;
import cn.textcheck.engine.pojo.LocalPaperLibrary;
import cn.textcheck.engine.pojo.Paper;
import cn.textcheck.engine.report.Reporter;
import cn.textcheck.engine.type.ReportType;
import cn.textcheck.starter.EasyStarter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class HomeworkServiceImpl implements HomeworkService {

    @Autowired
    private HdfsService hdfsService;
    @Autowired
    private HomeworkDao homeworkDao;
    @Autowired
    private HomeworkStudentDao homeworkStudentDao;
    @Autowired
    private HomeworkClazzDao homeworkClazzDao;

    @Autowired
    private StudentService studentService;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private ClazzService clazzService;

    @Override
    public Homework save(String[] clazzIdArr, Homework homeWork) {

        //1. 保存作业
        Homework homeworkRes = homeworkDao.save(homeWork);

        //2. 把hdfs tmp文件转移到相应的文件路径
        String oldPathStr = homeWork.getFilePath();
        String newPathStr = "/checkduplicate/homework/college/subject/" + homeworkRes.getId() +"/";
        try {
            if(!hdfsService.renameFile(oldPathStr , newPathStr, homeWork.getFileRandomName())){
                throw new CDException("文件系统发生异常，请尝试重新提交");
            }
        } catch (Exception e) {
            throw new CDException("文件系统发生异常，请尝试重新提交");
        }

        //3. 修改数据库文件路径
//        homeWork.setFilePath(newPath + oldPath.substring(oldPath.lastIndexOf("/") ,oldPath.length()));
        homeworkRes = homeworkDao.save(homeWork);


        List<HomeworkStudent> homeworkStudentList = new ArrayList<>();

        //4. 保存作业-班级List
        List<HomeworkClazz> homeworkClazzList = new ArrayList<>();
        for (String clazzId : clazzIdArr) {
            HomeworkClazz homeworkClazz = new HomeworkClazz();
            homeworkClazz.setHomeworkId(homeworkRes.getId());
            homeworkClazz.setClazzId(Integer.valueOf(clazzId));
            homeworkClazz.setSubmitted(0);
            homeworkClazz.setTotal(0);
            homeworkClazz.setState(1);
            homeworkClazzList.add(homeworkClazz);

            List<Student> studentList = studentService.findAllByClazzId(homeworkClazz.getClazzId());
            for (Student student : studentList) {
                HomeworkStudent homeworkStudent = new HomeworkStudent();
                homeworkStudent.setHomeworkId(homeworkRes.getId());
                homeworkStudent.setStudentId(student.getId());
                homeworkStudent.setClazzId(homeworkClazz.getClazzId());
                homeworkStudent.setState(1); //可用
                homeworkStudent.setSubmitted(0); //未提交
//                homeworkStudent.setRepeatRate(0.0f); //重复率
                homeworkStudent.setIsCheck(0); //未查重
                homeworkStudentList.add(homeworkStudent);
            }
        }
        homeworkClazzDao.saveAll(homeworkClazzList);

        //5. 保存作业-学生List
        homeworkStudentDao.saveAll(homeworkStudentList);

        return homeworkRes;
    }

    @Override
    public Page<HomeworkStudent> listPageForStudent(Integer page, Integer limit, Integer submitted
            , String startTime, String deadline, Integer studentId) {

        Pageable pageable = PageRequest.of(page -1,limit);


        Specification specification = (root, cq, cb) -> {

            Predicate predicate = cb.conjunction();
            //增加筛选条件0(当前学生id)
            predicate.getExpressions().add(cb.equal(root.get("studentId"), studentId));

            //增加筛选条件1(可用)
            predicate.getExpressions().add(cb.equal(root.get("state"), 1));
            //增加筛选条件2（日期）
            {
                if(StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(deadline)){
                    predicate.getExpressions()
                            .add(cb.between(root.get("deadline"), Timestamp.valueOf(startTime), Timestamp.valueOf(deadline)));
                }else if(StringUtils.isNotBlank(startTime)){
                    predicate.getExpressions()
                            .add(cb.between(root.get("deadline"), Timestamp.valueOf(startTime), Timestamp.valueOf("9999-12-12 23:59:59")));
                }else if(StringUtils.isNotBlank(deadline)){
                    predicate.getExpressions().add(cb.between(root.get("deadline"), Timestamp.valueOf("1970-01-01 00:00:00"), Timestamp.valueOf(deadline)));
                }
            }
            //增加筛选条件3（提交状态）
            if (submitted != null && submitted != -1){
                predicate.getExpressions().add(cb.equal(root.get("submitted"), submitted));
            }
            //增加筛选条件4（截止日期排序）
            cq.orderBy(cb.desc(root.get("deadline")));
            return predicate;
        };

        return (Page<HomeworkStudent>) homeworkStudentDao.findAll(specification,pageable);
    }

    @Override
    public Page<HomeworkStudent> listByPageForDetails(Integer page, Integer limit,
             Integer homeworkId, Integer submitted, String startTime, String deadline, Integer isCheck, Integer clazzId) {

        Pageable pageable = PageRequest.of(page -1,limit);

        Specification specification = (root, cq, cb) -> {
            Predicate predicate = cb.conjunction();
            //增加筛选条件-2(作业id)
            predicate.getExpressions().add(cb.equal(root.get("homeworkId"), homeworkId));

            //增加筛选条件-1(查重情况)
            if(isCheck != null && isCheck!=-1){
                predicate.getExpressions().add(cb.equal(root.get("state"), isCheck));
            }
            //增加筛选条件0(班级id)
            if(clazzId != null && clazzId!=-1){
                predicate.getExpressions().add(cb.equal(root.get("clazzId"), clazzId));
            }

            //增加筛选条件1(可用)
            predicate.getExpressions().add(cb.equal(root.get("state"), 1));

            //增加筛选条件2（日期）
            {
                if(StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(deadline)){
                    predicate.getExpressions()
                            .add(cb.between(root.get("deadline"), Timestamp.valueOf(startTime), Timestamp.valueOf(deadline)));
                }else if(StringUtils.isNotBlank(startTime)){
                    predicate.getExpressions()
                            .add(cb.between(root.get("deadline"), Timestamp.valueOf(startTime), Timestamp.valueOf("9999-12-12 23:59:59")));
                }else if(StringUtils.isNotBlank(deadline)){
                    predicate.getExpressions().add(cb.between(root.get("deadline"), Timestamp.valueOf("1970-01-01 00:00:00"), Timestamp.valueOf(deadline)));
                }
            }
            //增加筛选条件3（提交状态）
            if (submitted != null && submitted != -1){
                predicate.getExpressions().add(cb.equal(root.get("submitted"), submitted));
            }
            //增加筛选条件4（截止日期排序）
            cq.orderBy(cb.desc(root.get("deadline")));



            return predicate;
        };
        Page<HomeworkStudent> homeworkStudentPage = homeworkStudentDao.findAll(specification, pageable);
        return homeworkStudentPage;
    }

    @Override
    public HomeworkClazz findHomeworkClazzByHomeworkIdAndClazzId(Integer clazzId, Integer homeworkId) {
        return homeworkClazzDao.findByHomeworkIdAndClazzId(homeworkId,clazzId).get();
    }



    @Override
    public HomeworkStudent submitHomework(HomeworkStudent homeworkStudent, String tmpPath) {

        //1. 拼接HDFS存储学生作业附件路径
        String filePath = homeworkStudent.getFilePath(); //老师发布作业附件路径（目录）
        Integer clazzId = homeworkStudent.getClazzId(); //班级Id
        String studentFileRandomName = homeworkStudent.getStudentFileRandomName(); //学生提交作业附件随机名

        String oldPathStr = tmpPath;
//        String newPathStr = filePath + "/" + clazzId + "/";
        String newPathStr = filePath + "/"  +  homeworkStudent.getHomeworkName() + "-上交文件/" + homeworkStudent.getClazzName()  +"/" ;


        //2. 作业文件提交到HDFS（也就是移动）
        try {
//            if(!hdfsService.renameFile(oldPathStr , newPathStr, studentFileRandomName)){
//                throw new CDException("文件系统发生异常，请尝试重新提交");
//            }
            if(!FileUtils.renameFile(oldPathStr , newPathStr, studentFileRandomName)){
                throw new CDException("文件系统发生异常，请尝试重新提交");
            }
        } catch (Exception e) {
            throw new CDException("文件系统发生异常，请尝试重新提交");
        }


        //3. 修改作业的[总]上交人数
        Optional<Homework> homeworkOptional = homeworkDao.findById(homeworkStudent.getHomeworkId());
        Homework homework = homeworkOptional.get();
        homework.setSubmitted(homework.getSubmitted() + 1);
        homeworkDao.save(homework);

        //4. 修改作业的[班级]上交人数
        Optional<HomeworkClazz> homeworkClazzOptional = homeworkClazzDao.findByHomeworkIdAndClazzId(homeworkStudent.getHomeworkId() ,homeworkStudent.getClazzId());
        HomeworkClazz homeworkClazz = homeworkClazzOptional.get();
        homeworkClazz.setSubmitted(homeworkClazz.getSubmitted() + 1);
        homeworkClazzDao.save(homeworkClazz);

        //5. 修改作业-学生 记录
        Optional<HomeworkStudent> homeworkStudentOptional = homeworkStudentDao.findById(homeworkStudent.getId());
        HomeworkStudent homeworkStudentRes = homeworkStudentOptional.get();
        homeworkStudentRes.setSubmitted(1);
        homeworkStudentRes.setStudentFileName(homeworkStudent.getStudentFileName());
        homeworkStudentRes.setStudentFilePath(newPathStr);
        homeworkStudentRes.setStudentFileRandomName(studentFileRandomName);
        return homeworkStudentDao.save(homeworkStudentRes);
    }

    @Override
    public Page<Homework> listByPage(Integer page, Integer limit, Homework homework, String startTime) {
        //切记 datajpa从0页开始

        Pageable pageable = PageRequest.of(page -1,limit);

        Specification specification = (root, cq, cb) -> {

            Predicate predicate = cb.conjunction();
            //增加筛选条件1(可用)
            if (StringUtils.isNotBlank(homework.getName())){
                predicate.getExpressions().add(cb.like(root.get("name"), "%" + homework.getName() + "%"));
            }
            //增加筛选条件2(可用)
            if (StringUtils.isNotBlank(homework.getSubjectName())){
                predicate.getExpressions().add(cb.equal(root.get("subjectName"), "%" + homework.getSubjectName() + "%"));
            }

            //增加筛选条件3（日期）
            {
                if(StringUtils.isNotBlank(startTime) && homework.getDeadline() != null){
                    predicate.getExpressions()
                            .add(cb.between(root.get("deadline"), Timestamp.valueOf(startTime), homework.getDeadline()));
                }else if(StringUtils.isNotBlank(startTime)){
                    predicate.getExpressions()
                            .add(cb.between(root.get("deadline"), Timestamp.valueOf(startTime), Timestamp.valueOf("9999-12-12 23:59:59")));
                }else if(homework.getDeadline() != null){
                    predicate.getExpressions().add(cb.between(root.get("deadline"), Timestamp.valueOf("1970-01-01 00:00:00"), homework.getDeadline()));
                }
            }

            //增加筛选条件4（截止日期排序）
            cq.orderBy(cb.desc(root.get("deadline")));
            return predicate;
        };

        return homeworkDao.findAll(specification, pageable);
    }

    @Override
    public Page<Homework> listByPage(Integer page, Integer limit,
                                     String name, String subjectName,
                                     String startTime, String deadline,
                                     Integer teacherId) {
        //切记 datajpa从0页开始

        Pageable pageable = PageRequest.of(page -1,limit);

        Specification specification = (root, cq, cb) -> {

            Predicate predicate = cb.conjunction();

            //增加筛选条件0(当前学生id)
            predicate.getExpressions().add(cb.equal(root.get("teacherId"), teacherId));

            //增加筛选条件1(可用)
            if (StringUtils.isNotBlank(name)){
                predicate.getExpressions().add(cb.like(root.get("name"), "%" + name + "%"));
            }
            //增加筛选条件2(可用)
            if (StringUtils.isNotBlank(subjectName)){
                predicate.getExpressions().add(cb.like(root.get("subjectName"), "%" + subjectName + "%"));
            }

            //增加筛选条件3（日期）
            {
                if(StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(deadline)){
                    predicate.getExpressions()
                            .add(cb.between(root.get("deadline"), Timestamp.valueOf(startTime), Timestamp.valueOf(deadline)));
                }else if(StringUtils.isNotBlank(startTime)){
                    predicate.getExpressions()
                            .add(cb.between(root.get("deadline"), Timestamp.valueOf(startTime), Timestamp.valueOf("9999-12-12 23:59:59")));
                }else if(StringUtils.isNotBlank(deadline)){
                    predicate.getExpressions().add(cb.between(root.get("deadline"), Timestamp.valueOf("1970-01-01 00:00:00"), Timestamp.valueOf(deadline)));
                }
            }

            //增加筛选条件4（截止日期排序）
            cq.orderBy(cb.desc(root.get("deadline")));
            return predicate;
        };

        return homeworkDao.findAll(specification, pageable);
    }

    @Override
    public Homework save(String[] clazzIdArr, Homework homework, String tmpPath) {

        //2. 查询科目信息
        Subject subject = subjectService.findById(homework.getSubjectId());

        //1. 保存作业
        homework.setSubjectName(subject.getName());
        Homework homeworkRes = homeworkDao.save(homework);


        //3. 把hdfs tmp文件转移到相应的文件路径
        String oldPathStr = tmpPath;
        //1默认学院id
        String newPathStr = "/checkduplicate/homework/" +  "1" + "/"+  subject.getId() + "/" + homeworkRes.getId() +"/";
        try {
//            if(!hdfsService.renameFile(oldPathStr , newPathStr, homework.getFileRandomName())){
//                throw new CDException("文件系统发生异常，请尝试重新提交");
//            }
            if(!FileUtils.renameFile(oldPathStr , newPathStr, homework.getFileRandomName())){
                throw new CDException("文件系统发生异常，请尝试重新提交");
            }
        } catch (Exception e) {
            throw new CDException("文件系统发生异常，请尝试重新提交");
        }

        //3. 修改作业数据库文件路径 和 学科信息
        homeworkRes.setFilePath(newPathStr);
        homeworkRes.setSubjectName(subject.getName());
        homeworkRes = homeworkDao.save(homeworkRes);


        List<HomeworkStudent> homeworkStudentList = new ArrayList<>();

        //4. 保存作业-班级List
        List<HomeworkClazz> homeworkClazzList = new ArrayList<>();
        for (String clazzId : clazzIdArr) {
            //4-1. 查询班级
            Clazz clazz = clazzService.findById(Integer.valueOf(clazzId));

            HomeworkClazz homeworkClazz = new HomeworkClazz();
            homeworkClazz.setHomeworkId(homeworkRes.getId());
            homeworkClazz.setClazzId(Integer.valueOf(clazzId));
            homeworkClazz.setClazzName(clazz.getName());
            homeworkClazz.setSubmitted(0);
            homeworkClazz.setTotal(clazz.getNumbers());
            homework.setTotal(clazz.getNumbers() + homework.getTotal());

            homeworkClazz.setState(1);
            homeworkClazzList.add(homeworkClazz);

            List<Student> studentList = studentService.findAllByClazzId(homeworkClazz.getClazzId());
            for (Student student : studentList) {
                HomeworkStudent homeworkStudent = new HomeworkStudent();
                homeworkStudent.setHomeworkId(homeworkRes.getId());
                homeworkStudent.setHomeworkName(homeworkRes.getName());
                homeworkStudent.setHomeworkContent(homeworkRes.getContent());

                homeworkStudent.setStudentId(student.getId());
                homeworkStudent.setStudentName(student.getName());
                homeworkStudent.setSubjectName(subject.getName());
                homeworkStudent.setClazzId(homeworkClazz.getClazzId());
                homeworkStudent.setClazzName(homeworkClazz.getClazzName());

                homeworkStudent.setState(1); //可用
                homeworkStudent.setSubmitted(0); //未提交
                homeworkStudent.setIsCheck(0); //未查重

                homeworkStudent.setCodeRepeat(0.0f);
                homeworkStudent.setImgRepeat(0.0f);
                homeworkStudent.setTextRepeat(0.0f);

                homeworkStudent.setFilePath(homeworkRes.getFilePath());
                homeworkStudent.setFileName(homeworkRes.getFileName());
                homeworkStudent.setFileRandomName(homeworkRes.getFileRandomName());

                homeworkStudent.setDeadline(homeworkRes.getDeadline());

                homeworkStudentList.add(homeworkStudent);
            }
        }
        homeworkClazzDao.saveAll(homeworkClazzList);

        //5. 保存作业-学生List
        homeworkStudentDao.saveAll(homeworkStudentList);

        return homeworkRes;
    }

    @Override
    public void del(List<Homework> homeworkList) {
        try {
            //1. 删除作业
            homeworkDao.deleteAll(homeworkList);

            //2. 创建作业-班级集合 / 作业-学生集合
            List<Integer> homeworkIdList = new ArrayList<>();

            List<HomeworkClazz> homeworkClazzList = new ArrayList<>();
            List<HomeworkStudent> homeworkStudentList = new ArrayList<>();
            for (Homework homework : homeworkList) {
                homeworkClazzList.addAll(homeworkClazzDao.findAllByHomeworkId(homework.getId()));
                homeworkStudentList.addAll(homeworkStudentDao.findAllByHomeworkId(homework.getId()));

            }



            //3. 删除下属作业-班级
            homeworkClazzDao.deleteAll(homeworkClazzList);
            //4. 删除下属作业-学生
            homeworkStudentDao.deleteAll(homeworkStudentList);

        } catch (Exception e){
            throw new CDException(ExceptionDefault.DEL_ERROR);
        }
    }

    @Override
    public List<HomeworkStudent> listHomeworkStudent(Integer id) {
        return homeworkStudentDao.findAllByHomeworkId(id);
    }

    @Override
    public List<HomeworkClazz> listHomeworkClazz(Integer homeworkId) {
        return homeworkClazzDao.findAllByHomeworkId(homeworkId);
    }

    @Override
    public Homework update(Homework homework, String tmpPath) {
        //1.查询作业
        Homework oldhomework = homeworkDao.findById(homework.getId()).get();

        //2.修改作业信息
        if (StringUtils.isNotBlank(homework.getName())){
            oldhomework.setName(homework.getName());
        }
        if (StringUtils.isNotBlank(homework.getContent())){
            oldhomework.setContent(homework.getContent());
        }
        if (homework.getSubjectId() != null){
            oldhomework.setSubjectId(homework.getSubjectId());
        }
        if (homework.getDeadline() != null){
            oldhomework.setDeadline(homework.getDeadline());
        }

        //3.查询当前作业-学生记录信息
        List<HomeworkStudent> homeworkStudentList = homeworkStudentDao.findAllByHomeworkId(homework.getId());
        //4.查询科目信息
        Subject subject = subjectService.findById(homework.getSubjectId());

        //5.修改当前作业-学生记录信息
        for (HomeworkStudent homeworkStudent : homeworkStudentList) {
            if (StringUtils.isNotBlank(homework.getName())){
                homeworkStudent.setHomeworkName(homework.getName());
            }
            if (StringUtils.isNotBlank(homework.getContent())){
                homeworkStudent.setHomeworkContent(homework.getContent());
            }
            if (homework.getSubjectId() != null){
                homeworkStudent.setSubjectName(subject.getName());
            }
            if (homework.getDeadline() != null){
                homeworkStudent.setDeadline(homework.getDeadline());
            }
        }
        //6. 修改文件信息
        if (StringUtils.isNotBlank(tmpPath)){
            //6-1. 把hdfs tmp文件转移到相应的文件路径
            String oldPathStr = tmpPath;
            String newPathStr = "/checkduplicate/homework/" +  subject.getId() + "/"+  subject.getId() + "/" + oldhomework.getId() +"/";
            try {
//                if(!hdfsService.renameFile(oldPathStr , newPathStr, homework.getFileRandomName())){
//                    throw new CDException("文件系统发生异常，请尝试重新提交");
//                }
                if(!FileUtils.renameFile(oldPathStr , newPathStr, homework.getFileRandomName())){
                    throw new CDException("文件系统发生异常，请尝试重新提交");
                }
            } catch (Exception e) {
                throw new CDException("文件系统发生异常，请尝试重新提交");
            }

            //6-2.修改作业作业记录信息
            oldhomework.setFilePath(newPathStr);
            oldhomework.setFileName(homework.getFileName());
            oldhomework.setFileRandomName(homework.getFileRandomName());

            //6-3.修改当前作业-学生记录信息
            for (HomeworkStudent homeworkStudent : homeworkStudentList) {
                homeworkStudent.setFilePath(newPathStr);
                homeworkStudent.setFileName(homework.getFileName());
                homeworkStudent.setFileRandomName(homework.getFileRandomName());
            }
        }
        //7. 提交修改的作业
        homeworkDao.save(oldhomework);

        //8. 提交修改的作业-学生
        homeworkStudentDao.saveAll(homeworkStudentList);

        return oldhomework;
    }

    @Override
    public List<HomeworkStudent> checkAll(Integer homeworkId,Integer clazzId) throws IOException, InterruptedException {

        //0. 数据库查询相关数据
        Homework homework = homeworkDao.findById(homeworkId).get();

        //0.1 实现有无clazzId通用逻辑（有clazzId 查重班级，无查询本作业所有）
        List<HomeworkClazz> hcList = null;
//        if (clazzId != null){
            hcList = homeworkClazzDao.findAllByHomeworkId(homeworkId);
//        }else{
//            HomeworkClazz homeworkClazz = homeworkClazzDao.findByHomeworkIdAndClazzId(homeworkId, clazzId).get();
//            hcList = new ArrayList<>();
//            hcList.add(homeworkClazz);
//        }

        List<HomeworkStudent> hsList = null;
        if (clazzId != -1){
            hsList = homeworkStudentDao.findAllByHomeworkIdAndClazzId(homeworkId, clazzId);
        }else{
            hsList = homeworkStudentDao.findAllByHomeworkId(homeworkId);
        }



        //1. 设置验证
        CheckUtil.setRegCode();

        //2. 创建 CheckTask 构造类对象（taskBuilder）
        CheckTask.Builder taskBuilder = CheckManager.INSTANCE.getCheckTaskBuilder();

        //3. 添加作业下属班级目录下的文件到 => 本地库
        for (HomeworkClazz homeworkClazz : hcList) {
            //班级下属学生是否有上交记录
            if (homeworkClazz.getSubmitted() > 0){
                //班级文件夹
                File clazzDir = new File(FileUtils.getStaticPath() + homework.getFilePath() + homework.getName() + "-上交文件/" + homeworkClazz.getClazzName());
                //通过<文件夹>加载本地比对库（支持pdf、txt、doc、docx）
                LocalPaperLibrary paperLibrary = LocalPaperLibrary.load(clazzDir);
                //构造类对象 添加 本地库
                taskBuilder.addLibrary(paperLibrary);
            }
        }

        //4. 添加作业下属学生文件到 => 待查库
        List<Paper> paperList = new ArrayList<>();
        for (HomeworkStudent homeworkStudent : hsList) {
            //当前学生记录是否提交作业
            if (homeworkStudent.getSubmitted() == 1){
                //学生文件
                File studentFile = new File(FileUtils.getStaticPath() + homework.getFilePath() + homework.getName() + "-上交文件/" + homeworkStudent.getClazzName() + "/" + homeworkStudent.getStudentFileName());
                //通过<文件夹>加载待查文件（支持pdf、txt、doc、docx）
                Paper paper = Paper.load(studentFile);

                //设置作者做ID（作业-学生 => 名字、作业-学生id）
                paper.setId(String.valueOf(homeworkStudent.getId()))
                        .setAuthor(homeworkStudent.getStudentName());

                paper.setPayload(homeworkStudent);

                paperList.add(paper);
            }
        }
        taskBuilder.addCheckPaper(paperList);

        //5. 查重并获取结果
        CheckTask checkTask = taskBuilder.build();
        checkTask.start(); //启动任务
        checkTask.join(); //等待查重结束（阻塞）
        List<Reporter> reporters = checkTask.getReporters();

        //6.
        List<HomeworkStudent> checkedhomeworkStudentList = new ArrayList<>();
        for (Reporter reporter : reporters) {
            //6.0 从paper上下文获取homeworkStudent
            HomeworkStudent homeworkStudent = (HomeworkStudent) reporter.getPaper().getPayload();

            //6.1 设置查重结果
            homeworkStudent.setTextRepeat(Float.valueOf(reporter.getCopyRate()));
            homeworkStudent.setIsCheck(1);
            checkedhomeworkStudentList.add(homeworkStudent);
//            Integer id = Integer.valueOf(reporter.getPaper().getId());

            //6.2 查重结果保存到文件（详细结果）
            String saveDir = FileUtils.getStaticPath() + homework.getFilePath() + homeworkStudent.getHomeworkName()  + "-查重结果/" + homeworkStudent.getClazzName() + "/";
            File saveDirFile = new File(saveDir);
            if (!saveDirFile.exists()){
                saveDirFile.mkdirs();
            }
            reporter.saveAsFile(saveDir + "/" + homeworkStudent.getStudentFileName()  + ".html", ReportType.TEXT_WITH_ORIGINAL);
        }

        //7. 把查重的数据保存到数据库
        List<HomeworkStudent> res = homeworkStudentDao.saveAll(checkedhomeworkStudentList);
        return res;
    }

    @Override
    public List<HomeworkStudent> findHomeworkStudentByHomeworkIdAndYONClazzId(Integer homeworkId, Integer clazzId) {
        List<HomeworkStudent> hsList = null;
        if (clazzId != -1){
            hsList = homeworkStudentDao.findAllByHomeworkIdAndClazzId(homeworkId, clazzId);
        }else{
            hsList = homeworkStudentDao.findAllByHomeworkId(homeworkId);
        }
        return hsList;
    }


    @Override
    public void checkOne(HomeworkStudent homeworkStudent) throws IOException, InterruptedException {

        //0. 数据库查询相关数据
        List<HomeworkClazz> hcList = homeworkClazzDao.findAllByHomeworkId(homeworkStudent.getHomeworkId());

        //1. 设置验证
        CheckUtil.setRegCode();

        //2. 创建 CheckTask 构造类对象（taskBuilder）
        CheckTask.Builder taskBuilder = CheckManager.INSTANCE.getCheckTaskBuilder();

        //3. 添加作业下属班级目录下的文件到 => 本地库
        for (HomeworkClazz homeworkClazz : hcList) {
            //班级下属学生是否有上交记录
            if (homeworkClazz.getSubmitted() > 0) {
                //班级文件夹
                File clazzDir = new File(FileUtils.getStaticPath() + homeworkStudent.getFilePath() + homeworkClazz.getClazzName());
                //通过<文件夹>加载本地比对库（支持pdf、txt、doc、docx）
                LocalPaperLibrary paperLibrary = LocalPaperLibrary.load(clazzDir);
                //构造类对象 添加 本地库
                taskBuilder.addLibrary(paperLibrary);
            }
        }

        //4. 添加作业下属学生文件到 => 待查库
        {
            //学生文件
            File studentFile = new File(FileUtils.getStaticPath() + homeworkStudent.getFilePath() + homeworkStudent.getClazzName() + "/" + homeworkStudent.getStudentFileName());
            //通过<文件夹>加载待查文件（支持pdf、txt、doc、docx）
            Paper paper = Paper.load(studentFile);

            //设置作者做ID（作业-学生 => 名字、作业-学生id）
            paper.setId(String.valueOf(homeworkStudent.getId()))
                    .setAuthor(homeworkStudent.getStudentName());

            taskBuilder.addCheckPaper(paper);
        }

        //5. 查重并获取结果
        CheckTask checkTask = taskBuilder.build();
        checkTask.start(); //启动任务
        checkTask.join(); //等待查重结束（阻塞）
        List<Reporter> reporters = checkTask.getReporters();

        //6.
        {
            //6.1 设置查重结果
            homeworkStudent.setTextRepeat(Float.valueOf(reporters.get(0).getCopyRate()));
            homeworkStudent.setIsCheck(1);

            //6.2 查重结果保存到文件（详细结果）
            String saveDir = FileUtils.getStaticPath() + homeworkStudent.getFilePath() + homeworkStudent.getHomeworkName() + "-查重结果/" + homeworkStudent.getClazzName() + "/";
            File saveDirFile = new File(saveDir);
            if (!saveDirFile.exists()) {
                saveDirFile.mkdirs();
            }

            reporters.get(0).saveAsFile(saveDir + "/" + homeworkStudent.getStudentFileName() + ".html", ReportType.TEXT_WITH_ORIGINAL);
        }

        //7. 把查重的数据保存到数据库
        HomeworkStudent res = homeworkStudentDao.save(homeworkStudent);
    }

    @Override
    public Homework findHomeworkById(Integer id) {
        return homeworkDao.findById(id).get();
    }

    @Override
    public Page<Homework> noSubmitStudentList(Integer page, Integer limit, Integer homeworkId, Integer clazzId) {


        return null;
    }
}
