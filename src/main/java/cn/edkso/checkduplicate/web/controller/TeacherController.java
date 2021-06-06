package cn.edkso.checkduplicate.web.controller;

import cn.edkso.checkduplicate.constant.ConfigDefault;
import cn.edkso.checkduplicate.constant.NormalDefault;
import cn.edkso.checkduplicate.entry.Teacher;
import cn.edkso.checkduplicate.enums.ResultEnum;
import cn.edkso.checkduplicate.exception.CDException;
import cn.edkso.checkduplicate.service.TeacherService;
import cn.edkso.checkduplicate.vo.ResultVO;
import cn.edkso.utils.ResultVOUtil;
import cn.edkso.utils.TokenUtils;
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

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


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

        if (StringUtils.isNotBlank(token)) {
            teacher = (Teacher) redisTemplate.opsForValue().get(token);
            if (teacher != null) {
                Map<String, Object> map = new HashMap<>();
                map.put(ConfigDefault.TEACHER_TOKEN_NAME, token);
                map.put("teacher", teacher);
                return ResultVOUtil.success(map);
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
            redisTemplate.opsForValue().set(token, teacher,24, TimeUnit.HOURS);

            //5. 返回
            Map<String, Object> map = new HashMap<>();
            map.put(ConfigDefault.TEACHER_TOKEN_NAME, token);
            map.put("teacher", teacher);
            return ResultVOUtil.success(map);
        }
        return ResultVOUtil.error(ResultEnum.LOGIN_ERROR);
    }


    @ApiOperation(value = "获取教师通过分页,可带查询条件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page" ,value = "当前页数", required = true),
            @ApiImplicitParam(name = "limit" ,value = "每页限制数", required = true),
            @ApiImplicitParam(name = "name" ,value = "班级名称", required = false),
            @ApiImplicitParam(name = "username" ,value = "班级名称", required = false),
    })

    @ApiOperationSupport(ignoreParameters = {"id","state","create_time","update_time"})
    @GetMapping("listByPageForManager")
    public ResultVO listByPage(Integer page, Integer limit, Teacher teacher){
        Page<Teacher> clazzPage = teacherService.listByPage(page, limit,teacher);
        return ResultVOUtil.success(clazzPage);
    }

    @ApiOperation(value = "教师修改 - 管理员操作修改")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id" ,value = "教师id", required = true),
            @ApiImplicitParam(name = "password" ,value = "修改的密码", required = false),
            @ApiImplicitParam(name = "name" ,value = "管理员姓名", required = false),
            @ApiImplicitParam(name = "state" ,value = "管理员状态", required = false),
    })
    @ApiOperationSupport(ignoreParameters = {"id","username","createTime","updateTime"})
    @PostMapping("updateForManager")
    public ResultVO update(Teacher teacher){

        Teacher resTeacher = teacherService.updateForManager(teacher);
        if (resTeacher != null){
            return ResultVOUtil.success(resTeacher);
        }
        return ResultVOUtil.error(ResultEnum.REGISTER_ERROR);
    }


    @ApiOperation(value = "教师删除 - 管理员操作删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "json" ,value = "需要删除记录组成的json", required = true),
    })
    @PostMapping("delForManager")
    public ResultVO delForManager(@RequestBody List<Teacher> teacherList){
        try {
            teacherService.delForManager(teacherList);
            return ResultVOUtil.success(NormalDefault.DEL_SUCCESS);
        }catch (CDException e){
            return ResultVOUtil.error(e.getMessage());
        }
    }

}
