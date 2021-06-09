package cn.edkso.checkduplicate;

import cn.textcheck.CheckManager;
import org.junit.jupiter.api.Test;



import java.io.IOException;
//@SpringBootTest
public class CommonTest {

    @Test
    public void getMachineCode(){
        System.out.println(CheckManager.INSTANCE.getMachineCode());
    }

    @Test
    public void excelTest() throws IOException {

//        File staticPathFile = FileUtils.getStaticPathFile();
//        File file = new File(staticPathFile, "/测试excel.xlsx");
//        FileInputStream in = new FileInputStream(file);
//
//
//        MultipartFile multipartFile = new MockMultipartFile("ceshi", in);
//
//        ReadExcelUtil readExcelUtil = new ReadExcelUtil();
//        readExcelUtil.readExcel(multipartFile);

    }
}
