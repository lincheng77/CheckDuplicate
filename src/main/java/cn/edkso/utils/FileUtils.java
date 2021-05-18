package cn.edkso.utils;

import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class FileUtils {

    public static String rename(MultipartFile file){
        String uploadFileName = file.getOriginalFilename();
        String suffix = uploadFileName.substring(uploadFileName.lastIndexOf("."));
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        uploadFileName = sf.format(new Date()) + (new Random().nextInt(99999)) + suffix;
        return uploadFileName;
    }
}
