package cn.edkso.checkduplicate.web.controller;

import cn.edkso.checkduplicate.constant.NormalDefault;
import cn.edkso.checkduplicate.entry.Subject;
import cn.edkso.checkduplicate.enums.ResultEnum;
import cn.edkso.checkduplicate.exception.CDException;
import cn.edkso.checkduplicate.service.SubjectService;
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

@Api(tags = "学科模块")
@RestController
@RequestMapping("/subject")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @ApiOperation(value = "获取所有学科")
    @GetMapping("listAll")
    public ResultVO listAll(){
        List<Subject> subjectList = subjectService.listAll();
        return ResultVOUtil.success(subjectList);
    }

    @ApiOperation(value = "获取学科通过分页,可带查询条件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page" ,value = "当前页数", required = true),
            @ApiImplicitParam(name = "limit" ,value = "每页限制数", required = true),
            @ApiImplicitParam(name = "name" ,value = "学科名称", required = false),
    })

    
    @ApiOperationSupport(ignoreParameters = {"id","state","create_time","update_time"})
    @GetMapping("listByPage")
    public ResultVO listByPage(Integer page, Integer limit, Subject subject){
        Page<Subject> subjectPage = subjectService.listByPage(page, limit,subject);
        return ResultVOUtil.success(subjectPage);
    }

    @ApiOperation(value = "增加一条学科记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name" ,value = "学科名称", required = true),
            @ApiImplicitParam(name = "state" ,value = "状态（是否可用）", required = true),
            @ApiImplicitParam(name = "introduction" ,value = "学科介绍", required = false),
    })
    @ApiOperationSupport(ignoreParameters = {"id","create_time","update_time"})
    @PostMapping("add")
    public ResultVO add(Subject subject){
        try {
            subject.setCollegeId(0);
            subject.setCollegeName("");
            Subject resSubject = subjectService.saveAndUpdate(subject);
            if (resSubject != null){
                return ResultVOUtil.success(resSubject);
            }
        }catch (CDException e){
            return ResultVOUtil.error(e.getMessage());
        }
        return ResultVOUtil.error(ResultEnum.PARAMS_ERROR_OR_SYSTEM_EXCEPTION);
    }

    @ApiOperation(value = "修改一条学科记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id" ,value = "学科ID", required = true),
            @ApiImplicitParam(name = "name" ,value = "学科名称", required = false),
            @ApiImplicitParam(name = "introduction" ,value = "学科介绍", required = false),
            @ApiImplicitParam(name = "state" ,value = "状态（是否可用）", required = false),
    })
    @ApiOperationSupport(ignoreParameters = {"create_time","update_time"})
    @PostMapping("update")
    public ResultVO update(Subject subject){
        Subject resSubject = subjectService.saveAndUpdate(subject);
        if (resSubject != null){
            return ResultVOUtil.success(resSubject);
        }
        return ResultVOUtil.error(ResultEnum.PARAMS_ERROR_OR_SYSTEM_EXCEPTION);
    }


    @ApiOperation(value = "删除一条或多条学科记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "json" ,value = "需要删除记录组成的json", required = true),
    })
    @PostMapping("del")
    public ResultVO del(@RequestBody List<Subject> subjectList){
        try {
            subjectService.del(subjectList);
            return ResultVOUtil.success(NormalDefault.DEL_SUCCESS);
        }catch (CDException e){
            return ResultVOUtil.error(e.getMessage());
        }
    }

    @ApiOperation(value = "通过like学科名称获得学科记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name" ,value = "模糊查询学科名", required = true),
    })
    @PostMapping("listByNameLike")
    public ResultVO listByNameLike(String name){

        List<Subject> subjectList = subjectService.listByNameLike(name);
        return ResultVOUtil.success(subjectList);

    }
}
