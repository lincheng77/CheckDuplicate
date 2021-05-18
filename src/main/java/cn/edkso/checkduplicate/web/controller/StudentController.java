package cn.edkso.checkduplicate.web.controller;

import cn.edkso.checkduplicate.constant.ConfigDefault;
import cn.edkso.checkduplicate.entry.Student;
import cn.edkso.checkduplicate.entry.Teacher;
import cn.edkso.checkduplicate.enums.ResultEnum;
import cn.edkso.checkduplicate.service.StudentService;
import cn.edkso.checkduplicate.vo.ResultVO;
import cn.edkso.checkduplicate.vo.TokenVO;
import cn.edkso.utils.ResultVOUtil;
import cn.edkso.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


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
    public ResultVO login(String username, String password, HttpServletRequest request){
        //1. 通过token，从redis获取用户
        String access_token = request.getHeader(ConfigDefault.TOKEN_NAME);
        Student student = (Student) redisTemplate.opsForValue().get(access_token);

        if (student != null){
            return ResultVOUtil.success(student);
        }else{
            //2. 从db数据库获取用户
            student = studentService.login(username,password);
            if (student != null){

                //3. 生成token
                access_token = TokenUtils.token(username, password);
                //4. 把（token，用户）存储到redis
                redisTemplate.opsForValue().set(access_token, student);
                //5. 返回
                return ResultVOUtil.success(student, new TokenVO(access_token));
            }
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
}
