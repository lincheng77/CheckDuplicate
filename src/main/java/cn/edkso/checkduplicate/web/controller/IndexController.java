/*
 * Copyright (C) 2018 Zhejiang xiaominfo Technology CO.,LTD.
 * All rights reserved.
 * Official Web Site: http://www.xiaominfo.com.
 * Developer Web Site: http://open.xiaominfo.com.
 */

package cn.edkso.checkduplicate.web.controller;

import cn.edkso.checkduplicate.service.impl.HdfsService;
import cn.edkso.checkduplicate.vo.ResultVO;
import cn.edkso.utils.ResultVOUtil;
import cn.textcheck.CheckManager;
import cn.textcheck.engine.pojo.CheckResult;
import cn.textcheck.engine.pojo.Pair;
import cn.textcheck.engine.pojo.Paper;
import cn.textcheck.engine.report.HtmlTemplate;
import cn.textcheck.engine.report.Reporter;
import cn.textcheck.starter.EasyStarter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author <a href="mailto:xiaoymin@foxmail.com">xiaoymin@foxmail.com</a>
 * 2020/11/07 9:26
 * @since:knife4j-spring-boot-fast-demo 1.0
 */
@Api(tags = "首页模块")
@RestController
@RequestMapping("/test")
public class IndexController {

    @Autowired
    private HdfsService hdfsService;

//    @ApiImplicitParam(name = "name",value = "姓名",required = true)
//    @ApiOperation(value = "向客人问好")
//    @GetMapping("/sayHi")
//    public ResponseEntity<String> sayHi(@RequestParam(value = "name")String name){
//        return ResponseEntity.ok("Hi:"+name);
//    }


    @ApiOperation(value = "获取查重sdk指纹")
    @GetMapping("/getMachineCode")
    public ResultVO getMachineCode(){
        System.out.println(CheckManager.INSTANCE.getMachineCode());

        return ResultVOUtil.success(CheckManager.INSTANCE.getMachineCode());
    }


    @ApiOperation(value = "查重测试")
    @GetMapping("/testCheck")
    public ResultVO testCheck(){
        File classpath = null;


        //获取机器指纹
        System.out.println(CheckManager.INSTANCE.getMachineCode());
        //设置授权许可证（免费获取评估许可证：https://xincheck.com/?id=7）。授权许可证只需要设置1次，整个程序运行周期内均有效
//        CheckManager.INSTANCE.setRegCode("cN9N/aHzBuxf8NRybIoz2KS7EI2iSy7HF03sCByMHIclEWHMnjSLncMimT+fhlHoEuejzKSObe5P5RmAUrYPuEne8WGDGOwzW10RD2WvuQPpJp8344fHx232AkHtoCGRd1OWPtNfWj6LbUU42+cilwgWpLZPthlCCrd8Rl8r+SzUCsZY1RquHJanCpEuMfv5gJ1gP19GYmyMe9FnPypoDQcc5S/SQ/YevfE9Kx59Mow+eDyTR3c4MCjgPC0jzBDC3dRGJikAUeH86QqRLfOgNbXIw1EZeqMq+eSJsxOZN+1IWGSbmW/sbY6Z7mUZ8zMRkrY6mvetIhWVHi5XVVL1e9gLMt6rJkgR+r0O5Ff4XFOOxpO8O1gfs+uJm12F1RqarHCm/+X3VNEgieXoFWYatd6fsGFOfK9Yt/oRzwgCjLtT+kSY9Q6jyk1ZZZXKQPMVDa5MOSGdeYvwhFr5K4umYuTs1Rn7uVRmq7a//7JGczc=");
        CheckManager.INSTANCE.setRegCode("cbhUJXb/GhXSa0r9XCE/yvUv4Dywc5dbKhzuIEgKWzRuz6pXPg5CmrasWi7svizYtESqUgHjLwe6uDUQ9xp0yWJXp403E8q6Ar8cBqUc3GXt/o6Hn9yLR1rGCB9C3c7mKWf8xoW8YlhC80dkrrEhHfEe1KmDFYbju4Xs3DE4DFbsyqOy6tbnEHvLd/EGOXw5lQHcVOkMgbpKvEt6vPkZeRbWZ44yYSVOdI0Awkp9M6RgPqKMLSQ7st22W2XdoiThAlyVYCxPNdM1Ak4wqcEj8L3kUlvs9XTuxyPkjNb9rPQdElXlQk8sNPprkllFtnTPR5xrgoBohOyaeppNE/+JA5ncZ98LBsi/JwLtautfF3AOYkMW8y0C+IEvy/8IT22mrXBtwWs+Gm++DKBTWjIxt+6omZbHmxZ/A+E7syl5tP9u5Ev+yqvqlfxoKkg33e9Z44UYZThww04s06tucjQb96TUY8HKm7M0H37qWUTZ8TY=");





        //简易启动查重任务
//        try {
//            classpath = new File(ResourceUtils.getURL("classpath:").getPath());
//            FileSystem fs = hdfsService.getFileSystem();
//
//            Path path = new Path(classpath + "/static" + "/checkduplicate/homework/软件学院/1/26/");
//
////            FSDataInputStream in = fs.open(new Path("/checkduplicate/homework/软件学院/1/26"));
//            fs.
//            fs.copyToLocalFile(new Path("/checkduplicate/homework/软件学院/1/26/")
//            ,path );
//            IOUtils.


//            FileOutputStream out = new
//                    FileOutputStream(classpath + "/static" + "/checkduplicate/homework/软件学院/1/26");
//
//            byte[] b = new byte[1024 * 1024];
//            int read = 0;
//            while ((read = in.read(b)) > 0) {
//                out.write(b, 0, read);
//            }


//            List<Reporter> reporters = EasyStarter
//                    .check(file1,file2, null);
//            for (Reporter reporter : reporters) {
//                System.out.println(reporter.getPaper().getTitle());
//
//                System.out.println(reporter.getCopyRate());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return ResultVOUtil.success(CheckManager.INSTANCE.getMachineCode());
    }



    @ApiOperation(value = "查重测试2")
    @GetMapping("/testCheck2")
    public ResultVO testCheck2(){
        File classpath = null;
        try {
            classpath = new File(ResourceUtils.getURL("classpath:").getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //获取机器指纹
        System.out.println(CheckManager.INSTANCE.getMachineCode());
        //设置授权许可证（免费获取评估许可证：https://xincheck.com/?id=7）。授权许可证只需要设置1次，整个程序运行周期内均有效
        CheckManager.INSTANCE.setRegCode("cbhUJXb/GhXSa0r9XCE/yvUv4Dywc5dbKhzuIEgKWzRuz6pXPg5CmrasWi7svizYtESqUgHjLwe6uDUQ9xp0yWJXp403E8q6Ar8cBqUc3GXt/o6Hn9yLR1rGCB9C3c7mKWf8xoW8YlhC80dkrrEhHfEe1KmDFYbju4Xs3DE4DFbsyqOy6tbnEHvLd/EGOXw5lQHcVOkMgbpKvEt6vPkZeRbWZ44yYSVOdI0Awkp9M6RgPqKMLSQ7st22W2XdoiThAlyVYCxPNdM1Ak4wqcEj8L3kUlvs9XTuxyPkjNb9rPQdElXlQk8sNPprkllFtnTPR5xrgoBohOyaeppNE/+JA5ncZ98LBsi/JwLtautfF3AOYkMW8y0C+IEvy/8IT22mrXBtwWs+Gm++DKBTWjIxt+6omZbHmxZ/A+E7syl5tP9u5Ev+yqvqlfxoKkg33e9Z44UYZThww04s06tucjQb96TUY8HKm7M0H37qWUTZ8TY=");


        //简易启动查重任务
        try {
            File file1 = new File(classpath.getPath() + "/static");
            File file2 = new File(classpath.getPath() + "/static");

            List<Reporter> reporters = EasyStarter
                    .check(file1,file2, null);
            for (Reporter reporter : reporters) {
                System.out.println(reporter.getPaper().getTitle());

                System.out.println(reporter.getCopyRate());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return ResultVOUtil.success(CheckManager.INSTANCE.getMachineCode());
    }
}
