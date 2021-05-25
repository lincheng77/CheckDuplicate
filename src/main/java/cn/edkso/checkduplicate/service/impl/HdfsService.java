package cn.edkso.checkduplicate.service.impl;

import cn.edkso.checkduplicate.constant.ConfigDefault;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class HdfsService {

    private String HDFS_ADDRESS = ConfigDefault.HDFS_ADDRESS;
    private String HDFS_USER = ConfigDefault.HDFS_USER;

    /**
     * 获取HDFS配置信息
     * @return
     */
    private Configuration getConfiguration() {
        Configuration configuration = new Configuration();
        configuration.set("dfs.client.use.datanode.hostname", "true");
        return configuration;
    }


    /**
     * 获取HDFS文件系统对象
     * @return
     * @throws Exception
     */
    public FileSystem getFileSystem() throws Exception {
        // 客户端去操作hdfs时是有一个用户身份的，默认情况下hdfs客户端api会从jvm中获取一个参数作为自己的用户身份
        // DHADOOP_USER_NAME=hadoop
        // 也可以在构造客户端fs对象时，通过参数传递进去
        FileSystem fileSystem = FileSystem.get(new URI(HDFS_ADDRESS), getConfiguration(), HDFS_USER);
        return fileSystem;
    }


    /**
     * HDFS重命名文件 / 移动文件
     * oldPathStr 源文件目录+文件全名（带后缀）
     * newPathStr 新文件目录
     */
    @Deprecated
    public boolean renameFile(String oldPathStr, String newPathStr) throws Exception {
        if (StringUtils.isEmpty(oldPathStr) || StringUtils.isEmpty(newPathStr)) {
            return false;
        }
        FileSystem fs = getFileSystem();
        // 原文件目标路径
        Path oldPath = new Path(oldPathStr);
        // 重命名目标路径
        Path newPath = new Path(newPathStr);

        //判断新的目录是否存在，若不存在创建（调用直接创建会判断的方法）
        if (!mkdir(newPathStr)){
            return false;
        }

        //移动/复制  文件
        boolean isOk = fs.rename(oldPath, newPath);
        fs.close();
        return isOk;
    }

    /**
     * HDFS重命名文件 / 移动文件
     * oldPathStr 源文件目录+文件全名（带后缀）
     * newPathStr 新文件目录
     */
    public boolean renameFile(String oldPathStr, String newPathStr, String fileName) throws Exception {
        if (StringUtils.isEmpty(oldPathStr) || StringUtils.isEmpty(newPathStr) || StringUtils.isEmpty(fileName)) {
            return false;
        }
        FileSystem fs = getFileSystem();
        // 原文件目标路径
        Path oldPath = new Path(oldPathStr + fileName);
        // 重命名目标路径
        Path newPath = new Path(newPathStr);

        //判断路径是否存在，或者是否创建成功
        if (fs.exists(newPath) || fs.mkdirs(newPath)){
            //啊移动/复制文件
            boolean res = fs.rename(oldPath, newPath);
            fs.close();
            return res;
        }
        return false;
    }



    /**
     * 在HDFS创建文件夹
     * @param path
     * @return
     * @throws Exception
     */
    public boolean mkdir(String path) throws Exception {
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        if (existFile(path)) {
            return true;
        }
        FileSystem fs = getFileSystem();
        // 目标路径
        Path srcPath = new Path(path);
        boolean isOk = fs.mkdirs(srcPath);
        fs.close();
        return isOk;
    }

    /**
     * 判断HDFS文件是否存在,若不存在，=>创建
     * @param path
     * @return
     * @throws Exception
     */
    public  boolean existFile(String path) throws Exception {
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        FileSystem fs = getFileSystem();
        Path srcPath = new Path(path);
        boolean isExists = fs.exists(srcPath);
        return isExists;
    }

}
