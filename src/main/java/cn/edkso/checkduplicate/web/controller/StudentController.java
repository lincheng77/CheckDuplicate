package cn.edkso.checkduplicate.web.controller;

import cn.edkso.checkduplicate.constant.ConfigDefault;
import cn.edkso.checkduplicate.entry.Student;
import cn.edkso.checkduplicate.enums.ResultEnum;
import cn.edkso.checkduplicate.service.StudentService;
import cn.edkso.checkduplicate.vo.ResultVO;
import cn.edkso.utils.MD5Utils;
import cn.edkso.utils.ResultVOUtil;
import cn.edkso.utils.ServletUtils;
import cn.edkso.utils.TokenUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
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
}
