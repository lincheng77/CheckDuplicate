package cn.edkso.checkduplicate.web.controller;

import cn.edkso.checkduplicate.constant.ConfigDefault;
import cn.edkso.checkduplicate.entry.Student;
import cn.edkso.checkduplicate.entry.Teacher;
import cn.edkso.checkduplicate.enums.ResultEnum;
import cn.edkso.checkduplicate.service.TeacherService;
import cn.edkso.checkduplicate.vo.ResultVO;
import cn.edkso.checkduplicate.vo.TokenVO;
import cn.edkso.utils.ResultVOUtil;
import cn.edkso.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


//@CrossOrigin
@Api(tags = "教师模块")
@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;
    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation(value = "教师登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username" ,value = "教师共号", required = true),
            @ApiImplicitParam(name = "password" ,value = "教师密码", required = true),
    })
    @PostMapping("login")
    public ResultVO login(String username, String password, HttpServletRequest request){

        //1. 通过token，从redis获取用户
        String token = request.getHeader(ConfigDefault.TEACHER_TOKEN_NAME);
        Teacher teacher;
        if (token != null) {
            teacher = (Teacher) redisTemplate.opsForValue().get(token);
            if (teacher != null) {
                return ResultVOUtil.success(teacher);
            }
        }

        //2. 从db数据库获取用户
        teacher = teacherService.login(username, password);
        if (teacher != null) {
            //3. 生成token
            token = TokenUtils.token(username, password);
            if (token == null){
                return ResultVOUtil.error(ResultEnum.LOGIN_ERROR);
            }
            //4. 把（token，用户）存储到redis
            redisTemplate.opsForValue().set(token, teacher);

            //5. 返回
            Map<String, Object> map = new HashMap<>();
            map.put(ConfigDefault.TEACHER_TOKEN_NAME, token);
            map.put("teacher", teacher);
            return ResultVOUtil.success(map);
        }

        return ResultVOUtil.error(ResultEnum.LOGIN_ERROR);
    }
}
