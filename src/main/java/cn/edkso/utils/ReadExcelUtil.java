package cn.edkso.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: XXXX
 * @Time: 2020/5/28 0028
 * @Description: 解析excel工具类
 */
@Slf4j
public class ReadExcelUtil {

    /**
     * sheet中总行数
     */
    private int totalRows;

    /**
     * 每一行总单元格数
     */
    private static int totalCells;

    /**
     * read the Excel .xlsx,.xls
     *
     * @param file 上传的excel文件
     * @return
     * @throws IOException
     */
    public List<ArrayList<String>> readExcel(MultipartFile file) {
        if (file == null || ExcelUtil.EMPTY.equals(file.getOriginalFilename().trim())) {
            return null;
        } else {
            String postfix = ExcelUtil.getPostfix(file.getOriginalFilename());
            if (!ExcelUtil.EMPTY.equals(postfix)) {
                if (ExcelUtil.OFFICE_EXCEL_2003_POSTFIX.equals(postfix)) {
                    return readXls(file);
                } else if (ExcelUtil.OFFICE_EXCEL_2010_POSTFIX.equals(postfix)) {
                    return readXlsx(file);
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * read the Excel 2010 .xlsx
     *
     * @param file
     * @return
     * @throws IOException
     */
    @SuppressWarnings("deprecation")
    public List<ArrayList<String>> readXlsx(MultipartFile file) {
        List<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
        // IO流读取文件
        InputStream input = null;
        XSSFWorkbook wb = null;
        ArrayList<String> rowList = null;
        ArrayList<String> columeList = null;
        try {
            input = file.getInputStream();
            // 创建文档
            wb = new XSSFWorkbook(input);
            // 读取第一个sheet(页)
            XSSFSheet xssfSheet = wb.getSheetAt(0);
            totalRows = xssfSheet.getLastRowNum();

            // 控制
            if (totalRows > 4999) {
                totalRows = 4999;
            }
            // 获取第一行表头信息
            XSSFRow xssfRow1 = xssfSheet.getRow(0);
            if (xssfRow1 != null) {
                columeList = new ArrayList<String>();
                totalCells = xssfRow1.getLastCellNum();
                // 读取列，从第一列开始
                for (int c = 0; c <= totalCells + 1; c++) {
                    XSSFCell cell = xssfRow1.getCell(c);
                    if (cell == null || "".equals(cell)) {
                        break;
                    }
                    cell.setCellType(CellType.STRING);
                    columeList.add("1");
                }
            }
            // 读取Row,从第1行开始
            for (int rowNum = 0; rowNum <= totalRows; rowNum++) {
                XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                if (xssfRow == null) {
                    break;
                }

                if (xssfRow != null) {
                    rowList = new ArrayList<String>();
                    // 读取列，从第一列开始
                    for (int c = 0; c <= columeList.size() - 1; c++) {
                        XSSFCell cell = xssfRow.getCell(c);
                        if (cell == null || "".equals(cell)) {
                            rowList.add("");
                            continue;
                        }
                        // cell.setCellType(CellType.STRING);
                        rowList.add(ExcelUtil.getXValue(cell).trim());
                    }
                    list.add(rowList);
                }
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    /**
     * read the Excel 2003-2007 .xls
     *
     * @param file
     * @return
     * @throws IOException
     */
    public List<ArrayList<String>> readXls(MultipartFile file) {
        List<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
        // IO流读取文件
        InputStream input = null;
        HSSFWorkbook wb = null;
        ArrayList<String> rowList = null;
        ArrayList<String> columeList = null;
        try {
            input = file.getInputStream();
            // 创建文档
            wb = new HSSFWorkbook(input);
            //读取sheet(页)
            HSSFSheet hssfSheet = wb.getSheetAt(0);
            totalRows = hssfSheet.getLastRowNum();

            // 控制excel行数
            if (totalRows > 4999) {
                totalRows = 4999;
            }

            // 获取第一行表头信息数量
            HSSFRow row = hssfSheet.getRow(0);
            totalCells = row.getLastCellNum();
            columeList = new ArrayList<String>();
            for (int i = 0; i <= totalCells + 1; i++) {
                HSSFCell cell = row.getCell(i);
                if (cell == null || "".equals(cell)) {
                    break;
                }
                cell.setCellType(CellType.STRING);
                columeList.add("1");
            }


            //读取Row,从第1行开始
            for (int rowNum = 0; rowNum <= totalRows; rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    rowList = new ArrayList<String>();
                    //读取列，从第一列开始
                    for (short c = 0; c <= columeList.size() - 1; c++) {
                        HSSFCell cell = hssfRow.getCell(c);
                        if (cell == null || "".equals(cell)) {
                            rowList.add("");
                            continue;
                        }
                        // cell.setCellType(CellType.STRING);
                        rowList.add(ExcelUtil.getHValue(cell).trim());

                    }
                    list.add(rowList);
                }
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

