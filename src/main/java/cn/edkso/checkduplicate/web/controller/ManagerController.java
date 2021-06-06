package cn.edkso.checkduplicate.web.controller;

import cn.edkso.checkduplicate.constant.ConfigDefault;
import cn.edkso.checkduplicate.entry.Manager;
import cn.edkso.checkduplicate.enums.ResultEnum;
import cn.edkso.checkduplicate.service.ManagerService;
import cn.edkso.checkduplicate.vo.ResultVO;
import cn.edkso.utils.ResultVOUtil;
import cn.edkso.utils.TokenUtils;
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

@Api(tags = "管理员模块")
@RestController
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    private ManagerService managerService;
    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation(value = "管理员登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username" ,value = "管理员工号", required = true),
            @ApiImplicitParam(name = "password" ,value = "管理员密码", required = true),
    })
    @PostMapping("login")
    public ResultVO login(String username, String password, HttpServletRequest request){
        //1. 通过token，从redis获取用户
        String token = request.getHeader(ConfigDefault.MANAGER_TOKEN_NAME);
        Manager manager;

        if (StringUtils.isNotBlank(token)) {
            manager = (Manager) redisTemplate.opsForValue().get(token);
            if (manager != null) {
                Map<String, Object> map = new HashMap<>();
                map.put(ConfigDefault.MANAGER_TOKEN_NAME, token);
                map.put("manager", manager);
                return ResultVOUtil.success(map);
            }
        }

        //2. 从db数据库获取用户
        manager = managerService.login(username, password);
        if (manager != null) {
            //3. 生成token
            token = TokenUtils.token(username, password);
            if (token == null){
                return ResultVOUtil.error(ResultEnum.LOGIN_ERROR);
            }
            //4. 把（token，用户）存储到redis
            redisTemplate.opsForValue().set(token, manager,24, TimeUnit.HOURS);

            //5. 返回
            Map<String, Object> map = new HashMap<>();
            map.put(ConfigDefault.MANAGER_TOKEN_NAME, token);
            map.put("manager", manager);
            return ResultVOUtil.success(map);
        }
        return ResultVOUtil.error(ResultEnum.LOGIN_ERROR);
    }
}
