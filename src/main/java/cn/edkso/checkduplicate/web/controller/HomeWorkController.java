package cn.edkso.checkduplicate.web.controller;

import cn.edkso.checkduplicate.constant.ConfigDefault;
import cn.edkso.checkduplicate.entry.Clazz;
import cn.edkso.checkduplicate.entry.Homework;
import cn.edkso.checkduplicate.entry.HomeworkStudent;
import cn.edkso.checkduplicate.entry.Teacher;
import cn.edkso.checkduplicate.enums.ResultEnum;
import cn.edkso.checkduplicate.exception.CDException;

import cn.edkso.checkduplicate.service.HomeworkService;
import cn.edkso.checkduplicate.service.impl.HdfsService;
import cn.edkso.checkduplicate.vo.DataVO;
import cn.edkso.checkduplicate.vo.ResultVO;
import cn.edkso.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

    @ApiOperation(value = "增加一条作业记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clazzIds" ,value = "学科ids", required = true),
            @ApiImplicitParam(name = "subjectId" ,value = "作业科目", required = true),
            @ApiImplicitParam(name = "content" ,value = "作业内容", required = true),
            @ApiImplicitParam(name = "deadline" ,value = "截止日期", required = true),
            @ApiImplicitParam(name = "filePath" ,value = "作业附件路径", required = true),
    })
    @PostMapping("add")
    public ResultVO add(String clazzIds, String content,
                        Integer subjectId,
                        String filePath, @RequestParam Timestamp deadline){ //使用@RequestParam 可以让swagger多出来请求参数


        //1. 通过token，从redis获取用户
        String access_token = ServletUtils.getRequest().getHeader(ConfigDefault.TOKEN_NAME);
        Teacher teacher = (Teacher) redisTemplate.opsForValue().get(access_token);
        if(teacher == null){
            return ResultVOUtil.error(ResultEnum.NOT_LOGGED_IN); //没有登录
        }
        String[] clazzIdArr = clazzIds.split(",");
        Homework homework = new Homework();
        homework.setContent(content);
        homework.setDeadline(deadline);
        homework.setFilePath(filePath);
        homework.setSubjectId(subjectId);
        homework.setTeacherId(teacher.getId());
        try {
            Homework homeWorkRes = homeworkService.save(clazzIdArr, homework);
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
    @GetMapping("listPageForStudent")
    public ResultVO listPageForStudent(Integer page, Integer limit,
                                       Integer submitted,
                                       String startTime,
                                       String deadline){ //这里用String接收日期，后面先判断是字符串是否有效

        Page<HomeworkStudent> homeworkStudentPage = homeworkService.listPageForStudent(page, limit,submitted,startTime,deadline);
        return ResultVOUtil.success(homeworkStudentPage);
    }

    @ApiOperation(value = "提交作业")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "submitted" ,value = "提交状态", required = false),

            @ApiImplicitParam(name = "limit" ,value = "每页限制数", required = true),
    })
    @GetMapping("submitHomework")
    public ResultVO submitHomework(Integer id, Integer homeworkId, Integer clazzId,
                                   String fileName, String fileRandomName, String tmpPath){

        return null;
    }
    @ApiOperation(value = "文件上传")
    @RequestMapping("/upload")
    public ResultVO upload(MultipartFile file) {
        try {
            //1 获得文件输入流
            FileInputStream in = (FileInputStream) file.getInputStream();
            //2 创建连接
            Configuration conf = new Configuration();
            //3 获取连接对象
            FileSystem fs = FileSystem.get(URI.create(ConfigDefault.HDFS_ADDRESS), conf, ConfigDefault.HDFS_USER);

            //4 流上传文件
            String fileName = FileUtils.getFileName(file);
            String fileRandomName = FileUtils.rename(file);
            String tmpPath = "/checkduplicate/tmp/" + fileRandomName;
            FSDataOutputStream out = fs.create(new Path(tmpPath));//在hdfs上创建路径
            byte[] b = new byte[1024 * 1024];
            int read = 0;
            while ((read = in.read(b)) > 0) {
                out.write(b, 0, read);
            }

            //5 关闭资源
            in.close();
            out.close();
            fs.close();

            //6 返回
            Map<String,Object> map = new HashMap<>();
            map.put("fileName",fileName);
            map.put("fileRandomName",fileRandomName);
            map.put("tmpPath",tmpPath);
            return ResultVOUtil.success(map);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResultVOUtil.error(ResultEnum.UPLOAD_ERROR);
        }
    }

    @ApiOperation(value = "文件下载")
    @GetMapping("/down")
    public ResultVO down(String filePath,String fileName,String fileRandomName) {
        System.out.println(filePath);
        System.out.println(fileName);
        System.out.println(fileRandomName);

        try {
            //获取hdfs对象
            FileSystem fs = hdfsService.getFileSystem();
            Path file = new Path(filePath + "/" + fileRandomName);

            //获取输入流
            FSDataInputStream in = fs.open(file);


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

            //关流
            IOUtils.closeStream(in);
            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("OVER");
        return null;
    }
}