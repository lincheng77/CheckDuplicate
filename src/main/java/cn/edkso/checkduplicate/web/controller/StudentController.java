package cn.edkso.checkduplicate.web.controller;

import cn.edkso.checkduplicate.constant.ConfigDefault;
import cn.edkso.checkduplicate.constant.NormalDefault;
import cn.edkso.checkduplicate.entry.Student;
import cn.edkso.checkduplicate.enums.ResultEnum;
import cn.edkso.checkduplicate.exception.CDException;
import cn.edkso.checkduplicate.service.StudentService;
import cn.edkso.checkduplicate.vo.ResultVO;
import cn.edkso.utils.*;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


//@CrossOrigin
@Api(tags = "学生模块")
@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;
    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation(value = "学生登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username" ,value = "学生学号", required = true),
            @ApiImplicitParam(name = "password" ,value = "账号密码", required = true),
    })
    @PostMapping("login")
    public ResultVO login(String username, String password, HttpServletRequest request) {
        //1. 通过token，从redis获取用户
        String token = request.getHeader(ConfigDefault.STUDENT_TOKEN_NAME);
        Student student;

        if (StringUtils.isNotBlank(token)) {
            student = (Student) redisTemplate.opsForValue().get(token);
            if (student != null) {
                Map<String, Object> map = new HashMap<>();
                map.put(ConfigDefault.STUDENT_TOKEN_NAME, token);
                map.put("student", student);
                return ResultVOUtil.success(map);
            }
        }

        //2. 从db数据库获取用户
        student = studentService.login(username, password);
        if (student != null) {
            //3. 生成token
            token = TokenUtils.token(username, password);
            if (token == null){
                return ResultVOUtil.error(ResultEnum.LOGIN_ERROR);
            }
            //4. 把（token，用户）存储到redis
            redisTemplate.opsForValue().set(token, student,24, TimeUnit.HOURS);

            //5. 返回
            Map<String, Object> map = new HashMap<>();
            map.put(ConfigDefault.STUDENT_TOKEN_NAME, token);
            map.put("student", student);
            return ResultVOUtil.success(map);
        }
        return ResultVOUtil.error(ResultEnum.LOGIN_ERROR);
    }

    @ApiOperation(value = "学生注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username" ,value = "学生学号", required = true),
            @ApiImplicitParam(name = "password" ,value = "账号密码", required = true),
            @ApiImplicitParam(name = "name" ,value = "学生姓名", required = true)
    })
    @PostMapping("register")
    public ResultVO register(String username, String password, String name){
        Student student = studentService.register(username,password,name);
        if (student != null){
            return ResultVOUtil.success(student);
        }
        return ResultVOUtil.error(ResultEnum.REGISTER_ERROR);
    }

    @ApiOperation(value = "学生修改")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clazzId" ,value = "班级Id", required = false),
            @ApiImplicitParam(name = "oldPassword" ,value = "旧账号密码", required = false),
            @ApiImplicitParam(name = "password" ,value = "修改的密码", required = false),
            @ApiImplicitParam(name = "name" ,value = "学生姓名", required = false),
            @ApiImplicitParam(name = "state" ,value = "学生状态", required = false),
    })
    @ApiOperationSupport(ignoreParameters = {"id","username","createTime","updateTime"})
    @PostMapping("update")
    public ResultVO update(Student student, String oldPassword){
        //1. 通过token，从redis获取用户
        String access_token = ServletUtils.getRequest().getHeader(ConfigDefault.STUDENT_TOKEN_NAME);
        Student oldStudent = (Student) redisTemplate.opsForValue().get(access_token);
        if(oldStudent == null){
            return ResultVOUtil.error(ResultEnum.NOT_LOGGED_IN); //没有登录
        }

        if (oldPassword != null && !MD5Utils.md5(oldPassword).equals(oldStudent.getPassword())){
            return ResultVOUtil.error(ResultEnum.OLD_PASWORD_ERROR); //原始密码错误
        }

        if (StringUtils.isNotBlank(student.getPassword())){
            oldStudent.setPassword(MD5Utils.md5(student.getPassword()));
        }
        if(student.getClazzId() != null){
            oldStudent.setClazzId(student.getClazzId());
        }
        if(student.getState() != null){
            oldStudent.setState(student.getState());
        }

        if (StringUtils.isNotBlank(student.getName())){
            oldStudent.setName(student.getName());
        }

        Student studentRes = studentService.update(oldStudent);
        if (studentRes != null){
            return ResultVOUtil.success(studentRes);
        }
        return ResultVOUtil.error(ResultEnum.REGISTER_ERROR);
    }






    @ApiOperation(value = "学生导入注册")
    @PostMapping("/addListByImport")
    public ResultVO addListByImport(MultipartFile file, Integer clazzId) {


        String name=file.getOriginalFilename();
        if(name.length()<6|| !name.substring(name.length()-5).equals(".xlsx")){
            return ResultVOUtil.error("上传文件格式错误，请上传excle表格");
        }

        ReadExcelUtil readExcelUtil = new ReadExcelUtil();
        List<ArrayList<String>> excelList = readExcelUtil.readExcel(file);
        if (excelList.get(0).size() != 2){
            return ResultVOUtil.error("上传excel列数与模板列数不同");
        }
        if (!excelList.get(0).get(0).equals("学号") || !excelList.get(0).get(0).equals("学号")){
            return ResultVOUtil.error("上传excel列标题与模板列标题不同");
        }

        List<Student> studentList = studentService.addListByImport(excelList, clazzId);
        if (studentList != null){
            return ResultVOUtil.success(studentList);
        }
        return ResultVOUtil.error(ResultEnum.UPLOAD_ERROR);
    }



    @ApiOperation(value = "获取教师通过分页,可带查询条件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page" ,value = "当前页数", required = true),
            @ApiImplicitParam(name = "limit" ,value = "每页限制数", required = true),
            @ApiImplicitParam(name = "name" ,value = "学生名称", required = false),
            @ApiImplicitParam(name = "username" ,value = "学生账号（学号）", required = false),
            @ApiImplicitParam(name = "clazzId" ,value = "学生所在班级Id", required = false),
    })

    @ApiOperationSupport(ignoreParameters = {"id","state","create_time","update_time"})
    @GetMapping("listByPageForManager")
    public ResultVO listByPage(Integer page, Integer limit, Student student){
        Page<Student> studentPage = studentService.listByPage(page, limit,student);
        return ResultVOUtil.success(studentPage);
    }

    @ApiOperation(value = "学生修改 - 管理员操作修改")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id" ,value = "教师id", required = true),
            @ApiImplicitParam(name = "password" ,value = "修改的密码", required = false),
            @ApiImplicitParam(name = "name" ,value = "学生员姓名", required = false),
            @ApiImplicitParam(name = "state" ,value = "学生状态", required = false),
            @ApiImplicitParam(name = "clazzId" ,value = "学生所在班级Id", required = false),
            @ApiImplicitParam(name = "clazzName" ,value = "学生所在班级Id", required = false),
    })
    @ApiOperationSupport(ignoreParameters = {"id","username","createTime","updateTime"})
    @PostMapping("updateForManager")
    public ResultVO update(Student student){

        Student resStudent = studentService.updateForManager(student);
        if (resStudent != null){
            return ResultVOUtil.success(resStudent);
        }
        return ResultVOUtil.error(ResultEnum.REGISTER_ERROR);
    }


    @ApiOperation(value = "学生删除 - 管理员操作删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "json" ,value = "需要删除记录组成的json", required = true),
    })
    @PostMapping("delForManager")
    public ResultVO delForManager(@RequestBody List<Student> studentList){
        try {
            studentService.delForManager(studentList);
            return ResultVOUtil.success(NormalDefault.DEL_SUCCESS);
        }catch (CDException e){
            return ResultVOUtil.error(e.getMessage());
        }
    }
}
