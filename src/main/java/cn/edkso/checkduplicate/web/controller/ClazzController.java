package cn.edkso.checkduplicate.web.controller;

import cn.edkso.checkduplicate.constant.NormalDefault;
import cn.edkso.checkduplicate.entry.Clazz;
import cn.edkso.checkduplicate.enums.ResultEnum;
import cn.edkso.checkduplicate.exception.CDException;
import cn.edkso.checkduplicate.service.ClazzService;
import cn.edkso.checkduplicate.vo.ResultVO;
import cn.edkso.utils.ResultVOUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "班级模块")
@RestController
@RequestMapping("/clazz")
public class ClazzController {

    @Autowired
    private ClazzService clazzService;

    @ApiOperation(value = "获取所有班级")
    @GetMapping("listAll")
    public ResultVO listAll(){
        List<Clazz> clazzList = clazzService.listAll();
        return ResultVOUtil.success(clazzList);
    }

    @ApiOperation(value = "获取班级通过分页,可带查询条件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page" ,value = "当前页数", required = true),
            @ApiImplicitParam(name = "limit" ,value = "每页限制数", required = true),
            @ApiImplicitParam(name = "name" ,value = "班级名称", required = false),
            @ApiImplicitParam(name = "grade" ,value = "班级名称", required = false),
            @ApiImplicitParam(name = "college" ,value = "学院", required = false),
            @ApiImplicitParam(name = "counselor" ,value = "辅导员", required = false),
    })
    @ApiOperationSupport(ignoreParameters = {"id","state","create_time","update_time"})
    @GetMapping("listByPage")
    public ResultVO listByPage(Integer page, Integer limit, Clazz clazz){
        Page<Clazz> clazzPage = clazzService.listByPage(page, limit,clazz);
        return ResultVOUtil.success(clazzPage);
    }

    @ApiOperation(value = "增加一条班级记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name" ,value = "班级名称", required = true),
            @ApiImplicitParam(name = "numbers" ,value = "班级人数", required = true),
            @ApiImplicitParam(name = "grade" ,value = "班级年级", required = true),
            @ApiImplicitParam(name = "college" ,value = "学院", required = true),
            @ApiImplicitParam(name = "counselor" ,value = "辅导员", required = true),
            @ApiImplicitParam(name = "state" ,value = "状态（是否可用）", required = true),
    })
    @ApiOperationSupport(ignoreParameters = {"id","create_time","update_time"})
    @PostMapping("add")
    public ResultVO add(Clazz clazz){
        try {
            Clazz resClazz = clazzService.saveAndUpdate(clazz);
            if (resClazz != null){
                return ResultVOUtil.success(resClazz);
            }
        }catch (CDException e){
            return ResultVOUtil.error(e.getMessage());
        }
        return ResultVOUtil.error(ResultEnum.PARAMS_ERROR_OR_SYSTEM_EXCEPTION);
    }

    @ApiOperation(value = "修改一条班级记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id" ,value = "班级ID", required = true),
            @ApiImplicitParam(name = "name" ,value = "班级名称", required = false),
            @ApiImplicitParam(name = "numbers" ,value = "班级人数", required = true),
            @ApiImplicitParam(name = "grade" ,value = "班级年级", required = false),
            @ApiImplicitParam(name = "college" ,value = "学院", required = false),
            @ApiImplicitParam(name = "counselor" ,value = "辅导员", required = false),
            @ApiImplicitParam(name = "state" ,value = "状态", required = false),
    })
    @ApiOperationSupport(ignoreParameters = {"create_time","update_time"})
    @PostMapping("update")
    public ResultVO update(Clazz clazz){
        Clazz resClazz = clazzService.saveAndUpdate(clazz);
        if (resClazz != null){
            return ResultVOUtil.success(resClazz);
        }
        return ResultVOUtil.error(ResultEnum.PARAMS_ERROR_OR_SYSTEM_EXCEPTION);
    }


    @ApiOperation(value = "删除一条或多条班级记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "json" ,value = "需要删除记录组成的json", required = true),
    })
    @PostMapping("del")
//    public ResultVO del(String json){
    public ResultVO del(@RequestBody List<Clazz> clazzList){
        try {
            clazzService.del(clazzList);
            return ResultVOUtil.success(NormalDefault.DEL_SUCCESS);
        }catch (CDException e){
            return ResultVOUtil.error(e.getMessage());
        }
    }
}
