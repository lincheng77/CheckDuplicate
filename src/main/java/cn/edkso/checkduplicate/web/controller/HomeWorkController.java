package cn.edkso.checkduplicate.web.controller;

import cn.edkso.checkduplicate.constant.ConfigDefault;
import cn.edkso.checkduplicate.constant.NormalDefault;
import cn.edkso.checkduplicate.entry.*;
import cn.edkso.checkduplicate.enums.ResultEnum;
import cn.edkso.checkduplicate.exception.CDException;
import cn.edkso.checkduplicate.service.HomeworkService;
import cn.edkso.checkduplicate.service.impl.HdfsService;
import cn.edkso.checkduplicate.vo.ResultVO;
import cn.edkso.utils.FileUtils;
import cn.edkso.utils.ResultVOUtil;
import cn.edkso.utils.ServletUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "作业模块")
@RestController
@RequestMapping("/homework")
public class HomeWorkController {

    @Autowired
    private HomeworkService homeworkService;


    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private HdfsService hdfsService;

    @ApiOperation(value = "文件上传")
    @RequestMapping("/upload")
    public ResultVO upload(MultipartFile file) {
        try {
//            //1 获得文件输入流
//            FileInputStream in = (FileInputStream) file.getInputStream();
//            //2 创建连接
//            Configuration conf = new Configuration();
//            //3 获取连接对象
//            FileSystem fs = FileSystem.get(URI.create(ConfigDefault.HDFS_ADDRESS), conf, ConfigDefault.HDFS_USER);
//
////            FileSystem fs = hdfsService.getFileSystem();
//
//            //4 流上传文件
//            String fileName = FileUtils.getFileName(file);
//            String fileRandomName = FileUtils.rename(file);
////            String tmpPath = "/checkduplicate/tmp/" + fileRandomName;
//            String tmpPath = "/checkduplicate/tmp/";
//            FSDataOutputStream out = fs.create(new Path(tmpPath + fileRandomName));//在hdfs上创建路径
//            byte[] b = new byte[1024 * 1024];
//            int read = 0;
//            while ((read = in.read(b)) > 0) {
//                out.write(b, 0, read);
//            }
//
//            //5 关闭资源
//            in.close();
//            out.close();
//            fs.close();
            String fileName = FileUtils.getFileName(file);
            String fileRandomName = FileUtils.getFileRandomName(file);
            String tmpPath = "/checkduplicate/tmp/";
            FileUtils.saveFile(file,tmpPath, fileRandomName);


            //6 返回
            Map<String,Object> map = new HashMap<>();
            map.put("fileName",fileName);
            map.put("fileRandomName",fileRandomName);
            map.put("tmpPath",tmpPath);
            return ResultVOUtil.success(map);
//        } catch (IOException | InterruptedException e) {
        } catch (IOException e) {
            e.printStackTrace();
            return ResultVOUtil.error(ResultEnum.UPLOAD_ERROR);
        }
    }

    @ApiOperation(value = "增加一条作业记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clazzIds" ,value = "学科ids", required = true),
            @ApiImplicitParam(name = "name" ,value = "学科ids", required = true),
            @ApiImplicitParam(name = "content" ,value = "作业内容", required = true),
            @ApiImplicitParam(name = "fileName" ,value = "作业附件路径", required = true),
            @ApiImplicitParam(name = "tmpPath" ,value = "HDFS临时路径", required = true),
            @ApiImplicitParam(name = "fileRandomName" ,value = "作业附件随机名", required = true),
            @ApiImplicitParam(name = "subjectId" ,value = "作业科目", required = true),
            @ApiImplicitParam(name = "deadline" ,value = "截止日期", required = true),

    })
    @PostMapping("add")//使用@RequestParam 可以让swagger多出来请求参数
    public ResultVO add(String clazzIds,String name, String content,
                        String fileName, String tmpPath, String fileRandomName,
                        Integer subjectId, @RequestParam Timestamp deadline){

        //1. 通过token，从redis获取用户
        String access_token = ServletUtils.getRequest().getHeader(ConfigDefault.TEACHER_TOKEN_NAME);
        Teacher teacher = (Teacher) redisTemplate.opsForValue().get(access_token);
        if(teacher == null){
            return ResultVOUtil.error(ResultEnum.NOT_LOGGED_IN); //没有登录
        }
        String[] clazzIdArr = clazzIds.split(",");
        Homework homework = new Homework();
        homework.setName(name);
        homework.setContent(content);
        homework.setDeadline(deadline);

        homework.setState(1);
        homework.setSubmitted(0);
        homework.setTotal(0);

        homework.setFileName(fileName);
        homework.setFileRandomName(fileRandomName);

        homework.setSubjectId(subjectId);
        homework.setTeacherId(teacher.getId());

        //subjectName 和 path没有设置还
        try {
            Homework homeWorkRes = homeworkService.save(clazzIdArr, homework,tmpPath);
            if (homeWorkRes != null){
                return ResultVOUtil.success(homeWorkRes);
            }
        }catch (CDException e){
            return ResultVOUtil.error(e.getMessage());
        }
        return ResultVOUtil.error(ResultEnum.PARAMS_ERROR_OR_SYSTEM_EXCEPTION);
    }


    @ApiOperation(value = "查询当前学生作业记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "submitted" ,value = "提交状态", required = false),
            @ApiImplicitParam(name = "startTime" ,value = "开始日期", required = false),
            @ApiImplicitParam(name = "deadline" ,value = "截止日期", required = false),
            @ApiImplicitParam(name = "page" ,value = "当前页数", required = true),
            @ApiImplicitParam(name = "limit" ,value = "每页限制数", required = true),
    })
    @PostMapping("listPageForStudent")
    //这里用String接收日期，后面先判断是字符串是否有效
    public ResultVO listPageForStudent(Integer page, Integer limit, Integer submitted, String startTime, String deadline){

        //1. 通过token，从redis获取用户
        String access_token = ServletUtils.getRequest().getHeader(ConfigDefault.STUDENT_TOKEN_NAME);
        Student student = (Student) redisTemplate.opsForValue().get(access_token);
        if(student == null){
            return ResultVOUtil.error(ResultEnum.NOT_LOGGED_IN); //没有登录
        }


        Page<HomeworkStudent> homeworkStudentPage = homeworkService.listPageForStudent(page, limit,submitted,startTime,deadline, student.getId());
        return ResultVOUtil.success(homeworkStudentPage);
    }


    @ApiOperation(value = "查询当前教师作业-学生记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "submitted" ,value = "提交状态", required = false),
            @ApiImplicitParam(name = "homeworkId" ,value = "提交状态", required = true),
            @ApiImplicitParam(name = "isCheck" ,value = "提交状态", required = false),
            @ApiImplicitParam(name = "ClazzId" ,value = "提交状态", required = false),
            @ApiImplicitParam(name = "startTime" ,value = "开始日期", required = false),
            @ApiImplicitParam(name = "deadline" ,value = "截止日期", required = false),
            @ApiImplicitParam(name = "page" ,value = "当前页数", required = true),
            @ApiImplicitParam(name = "limit" ,value = "每页限制数", required = true),
    })
    @GetMapping("listByPageForDetails")
    //这里用String接收日期，后面先判断是字符串是否有效
    public ResultVO listByPageForDetails(Integer page, Integer limit,
                                         Integer homeworkId,
                                       Integer submitted,
                                       Integer isCheck,
                                       Integer clazzId,
                                       String startTime,
                                       String deadline){
        //1. 通过token，从redis获取用户
        String access_token = ServletUtils.getRequest().getHeader(ConfigDefault.TEACHER_TOKEN_NAME);
        Teacher teacher = (Teacher) redisTemplate.opsForValue().get(access_token);
        if(teacher == null){
            return ResultVOUtil.error(ResultEnum.NOT_LOGGED_IN); //没有登录
        }

        Page<HomeworkStudent> homeworkStudentPage = homeworkService.listByPageForDetails(page, limit,
                homeworkId,submitted,startTime,deadline, isCheck,clazzId);
        return ResultVOUtil.success(homeworkStudentPage);
    }


    @ApiOperation(value = "文件下载")
    @GetMapping("/down")
    public ResultVO down(String filePath,String fileName,String fileRandomName) {
        System.out.println(filePath);
        System.out.println(fileName);
        System.out.println(fileRandomName);

        try {
//            //获取hdfs对象
//            FileSystem fs = hdfsService.getFileSystem();
//            Path file = new Path(filePath + "/" + fileRandomName);
//
//            //获取输入流
//            FSDataInputStream in = fs.open(file);
            File file = new File(FileUtils.getStaticPath() + filePath + "/" + fileRandomName);

            FileInputStream in = new FileInputStream(file);

            //设置resposne响应头
            //设置响应头类型
            ServletContext servletContext = ServletUtils.getServletContext();
            HttpServletResponse response = ServletUtils.getResponse();
            String mimeTyep = servletContext.getMimeType(fileName);
            response.setHeader("content-type",mimeTyep);
//            response.setCharacterEncoding("utf-8"); //不知道干啥具体
            //设置响应头打开方式 );
            response.setHeader("content-disposition","attachment;filename="+ java.net.URLEncoder.encode(fileName, "UTF-8"));

            //获取输出流(具体到文件名)
            ServletOutputStream out = response.getOutputStream();

            //将输入流的数据写出到输出流中
            byte[] buf = new byte[1024*8];
            int len=0;
            while ((len = in.read(buf))!=-1){
                out.write(buf,0,len);
            }

//            //关流
//            IOUtils.closeStream(in);
//            fs.close();

            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("OVER");
        return null;
    }


    @ApiOperation(value = "提交作业")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id" ,value = "作业-学生Id", required = true),
            @ApiImplicitParam(name = "homeworkId" ,value = "作业Id", required = true),
            @ApiImplicitParam(name = "clazzId" ,value = "班级Id", required = true),
            @ApiImplicitParam(name = "filePath" ,value = "老师作业目录", required = true),
            @ApiImplicitParam(name = "studentFileName" ,value = "学生提交作业文件名", required = true),
            @ApiImplicitParam(name = "studentFileRandomName" ,value = "学生提交作业随机名", required = true),
            @ApiImplicitParam(name = "tmpPath" ,value = "学生提交作业HDFS临时路径", required = true),
    })

    @PostMapping("submitHomework")
    public ResultVO submitHomework(HomeworkStudent homeworkStudent, String tmpPath){
        //1. 通过token，从redis获取用户
        String access_token = ServletUtils.getRequest().getHeader(ConfigDefault.STUDENT_TOKEN_NAME);
        Student student = (Student) redisTemplate.opsForValue().get(access_token);
        if(student == null){
            return ResultVOUtil.error(ResultEnum.NOT_LOGGED_IN); //没有登录
        }


        try {
            HomeworkStudent homeworkStudentRes = homeworkService.submitHomework(homeworkStudent, tmpPath);
            if (homeworkStudentRes != null){
                return ResultVOUtil.success(homeworkStudentRes);
            }
        }catch (CDException e){
            return ResultVOUtil.error(e.getMessage());
        }
        return ResultVOUtil.error(ResultEnum.PARAMS_ERROR_OR_SYSTEM_EXCEPTION);

    }


    @ApiOperation(value = "查询当前教师作业记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page" ,value = "当前页数", required = true),
            @ApiImplicitParam(name = "limit" ,value = "每页限制数", required = true),
            @ApiImplicitParam(name = "name" ,value = "作业名称", required = false),
            @ApiImplicitParam(name = "subjectName" ,value = "学科名称", required = false),
            @ApiImplicitParam(name = "startTime" ,value = "开始日期", required = false),
            @ApiImplicitParam(name = "deadline" ,value = "截止日期", required = false),
    })
    @GetMapping("listByPage")
    public ResultVO listByPage(Integer page, Integer limit,
                               String name , String subjectName,
                               String startTime,String deadline){

        //1. 通过token，从redis获取用户
        String access_token = ServletUtils.getRequest().getHeader(ConfigDefault.TEACHER_TOKEN_NAME);
        Teacher teacher = (Teacher) redisTemplate.opsForValue().get(access_token);
        if(teacher == null){
            return ResultVOUtil.error(ResultEnum.NOT_LOGGED_IN); //没有登录
        }

        Page<Homework> homeworkPage = homeworkService.listByPage(page, limit,name,subjectName,startTime,deadline, teacher.getId());
        return ResultVOUtil.success(homeworkPage);
    }


    @ApiOperation(value = "删除一条或多条作业记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "json" ,value = "需要删除记录组成的json", required = true),
    })
    @PostMapping("del")
    public ResultVO del(@RequestBody List<Homework> homeworkList){
        try {
            homeworkService.del(homeworkList);
            return ResultVOUtil.success(NormalDefault.DEL_SUCCESS);
        }catch (CDException e){
            return ResultVOUtil.error(e.getMessage());
        }
    }

    @ApiOperation(value = "查询当前作业下属班级")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id" ,value = "作业id", required = true),
    })
    @PostMapping("listHomeworkClazz")
    public ResultVO listHomeworkClazz(Integer homeworkId){
        List<HomeworkClazz> homeworkClazzList = homeworkService.listHomeworkClazz(homeworkId);
        return ResultVOUtil.success(homeworkClazzList);
    }

    @ApiOperation(value = "查询当前作业下属学生")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id" ,value = "作业id", required = true),
    })
    @PostMapping("listHomeworkStudent")
    public ResultVO listHomeworkStudent(Integer id){
        List<HomeworkStudent> homeworkStudentList = homeworkService.listHomeworkStudent(id);
        return ResultVOUtil.success(homeworkStudentList);
    }


    @ApiOperation(value = "增加一条作业记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id" ,value = "作业id", required = false),
            @ApiImplicitParam(name = "name" ,value = "作业名称", required = false),
            @ApiImplicitParam(name = "content" ,value = "作业内容", required = false),
            @ApiImplicitParam(name = "fileName" ,value = "作业附件路径", required = false),
            @ApiImplicitParam(name = "tmpPath" ,value = "HDFS临时路径", required = false),
            @ApiImplicitParam(name = "fileRandomName" ,value = "作业附件随机名", required = false),
            @ApiImplicitParam(name = "subjectId" ,value = "作业科目", required = false),
            @ApiImplicitParam(name = "deadline" ,value = "截止日期", required = false),

    })
    @PostMapping("update")//使用@RequestParam 可以让swagger多出来请求参数
    public ResultVO update(Integer id, String name, String content,
                        String fileName, String tmpPath, String fileRandomName,
                        Integer subjectId, @RequestParam Timestamp deadline){

        Homework homework = new Homework();
        homework.setId(id);
        homework.setName(name);
        homework.setContent(content);
        homework.setDeadline(deadline);


        homework.setFileName(fileName);
        homework.setFileRandomName(fileRandomName);

        homework.setSubjectId(subjectId);

        try {
            Homework homeWorkRes = homeworkService.update(homework,tmpPath);
            if (homeWorkRes != null){
                return ResultVOUtil.success(homeWorkRes);
            }
        }catch (CDException e){
            return ResultVOUtil.error(e.getMessage());
        }
        return ResultVOUtil.error(ResultEnum.PARAMS_ERROR_OR_SYSTEM_EXCEPTION);
    }


    @ApiOperation(value = "查询一条（作业-班级）记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clazzId" ,value = "（作业-班级）id", required = true),
            @ApiImplicitParam(name = "homeworkId" ,value = "作业id", required = true),
    })
    @PostMapping("findHomeworkClazzByHomeworkIdAndClazzId")
    public ResultVO findHomeworkClazzByHomeworkIdAndClazzId(Integer clazzId,Integer homeworkId){

        HomeworkClazz homeworkClazz = homeworkService.findHomeworkClazzByHomeworkIdAndClazzId(clazzId, homeworkId);
        if (homeworkClazz != null){
            return ResultVOUtil.success(homeworkClazz);
        }
        return ResultVOUtil.error(ResultEnum.PARAMS_ERROR_OR_SYSTEM_EXCEPTION);
    }


    @ApiOperation(value = "单条记录查重")
    @ApiImplicitParams({
    })
    @PostMapping("checkOne")
    public ResultVO checkOne(HomeworkStudent homeworkStudent){
        try {
            homeworkService.checkOne(homeworkStudent);
            return ResultVOUtil.success();
        }catch (Exception e){
            return ResultVOUtil.error(ResultEnum.CHECK_ERROR);
        }

    }

    @ApiOperation(value = "单条记录查重")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id" ,value = "作业id", required = true),
    })
    @PostMapping("findHomeworkById")
    public ResultVO findHomeworkById(Integer id){
        Homework homework = homeworkService.findHomeworkById(id);
        if (homework != null){
            return ResultVOUtil.success(homework);
        }
        return ResultVOUtil.error(ResultEnum.PARAMS_ERROR_OR_SYSTEM_EXCEPTION);
    }


    @ApiOperation(value = "查询当前未交学生名单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page" ,value = "当前页数", required = true),
            @ApiImplicitParam(name = "limit" ,value = "每页限制数", required = true),
            @ApiImplicitParam(name = "homeworkId" ,value = "作业id", required = true),
            @ApiImplicitParam(name = "clazzId" ,value = "班级id", required = false),
    })
    @GetMapping("noSubmitStudentList")
    public ResultVO noSubmitStudentList(Integer page, Integer limit,
                                        Integer homeworkId, Integer clazzId){
        Page<Homework> homeworkPage = homeworkService.noSubmitStudentList(page, limit,homeworkId,clazzId);
        return ResultVOUtil.success(homeworkPage);
    }
}
