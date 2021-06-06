package cn.edkso.utils;

import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class FileUtils {

    public static String getFileRandomName(MultipartFile file){
        String uploadFileName = file.getOriginalFilename();
        String suffix = uploadFileName.substring(uploadFileName.lastIndexOf("."));
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        uploadFileName = sf.format(new Date()) + (new Random().nextInt(99999)) + suffix;
        return uploadFileName;
    }

    public static String getFileName(MultipartFile file){
        return file.getOriginalFilename();
    }


    public static String getClassPath() throws FileNotFoundException {
        return ResourceUtils.getURL("classpath:").getPath();
    }

    public static String getStaticPath() throws FileNotFoundException {
        return ResourceUtils.getURL("classpath:").getPath() + "/static";
    }

    public static File getClassPathFile() throws FileNotFoundException {
        File classPathFile = new File(getClassPath());
        if(!classPathFile.exists()) {
            classPathFile.mkdirs();
        }
        return classPathFile;
    }

    public static File getStaticPathFile() throws FileNotFoundException {
        File classPathFile = new File(getStaticPath());
        if(!classPathFile.exists()) {
            classPathFile.mkdirs();
        }
        return classPathFile;
    }

    /**
     *
     * @param path 文件要保存的相对路径
     * @param file 表单上传的文件
     * @return filePath 访问文件的相对路径
     * @throws IOException
     */
    public static void saveFile(MultipartFile file,String path, String fileRandomName) throws IOException {

        File dir = new File(getStaticPath() + path);
        if(!dir.exists()) {
            dir.mkdirs();
        }

        File save = new File(dir, fileRandomName);

        file.transferTo(save);
    }


    public static String saveFile(MultipartFile file,String path) throws IOException {
        String fileRandomName = getFileRandomName(file);
        File save = new File(getStaticPath() + path + fileRandomName);
        file.transferTo(save);
        return fileRandomName;
    }

    public static boolean renameFile(String oldPathStr ,String newPathStr,String  fileName) throws FileNotFoundException {
        File oldFile = new File(getStaticPath() + oldPathStr + "/" + fileName);
        File newFile = new File( getStaticPath() +newPathStr);
        if(!newFile.exists()) {
            boolean mkdirs = newFile.mkdirs();
        }

        newFile = new File(newFile , fileName);
        boolean b = oldFile.renameTo(newFile);
        return b;
    }
}
