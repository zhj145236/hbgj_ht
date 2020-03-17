package com.yusheng.hbgj.utils;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

/**
 * excel工具类
 * 
 * @author Jinwei
 *
 */
public class ExcelUtil {


    private final static String xls = "xls";
    private final static String xlsx = "xlsx";

    /**
     * 读入excel文件，解析后返回 List对象
     * @param file
     * @throws IOException
     */
    public static List<Object[]>  importExcel(MultipartFile file) throws IOException{
        //检查文件
        checkFile(file);
        //获得Workbook工作薄对象
        Workbook workbook = getWorkBook(file);
        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
        List<Object[]> list = new ArrayList<Object[]>();
        if(workbook != null){
            for(int sheetNum = 0;sheetNum < workbook.getNumberOfSheets();sheetNum++){
                //获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if(sheet == null){
                    continue;
                }
                //获得当前sheet的开始行
                int firstRowNum  = sheet.getFirstRowNum();
                //获得当前sheet的结束行
                int lastRowNum = sheet.getLastRowNum();
                //循环除了第一行的所有行
                for(int rowNum = firstRowNum+1;rowNum <= lastRowNum;rowNum++){
                    //获得当前行
                    Row row = sheet.getRow(rowNum);
                    if(row == null){
                        continue;
                    }
                    //获得当前行的开始列
                    int firstCellNum = row.getFirstCellNum();
                    //获得当前行的列数
                    int lastCellNum = row.getLastCellNum();
                    Object[] cells = new String[row.getLastCellNum()];
                    //循环当前行
                    for(int cellNum = firstCellNum; cellNum < lastCellNum;cellNum++){
                        Cell cell = row.getCell(cellNum);
                        cells[cellNum] = getCellValue(cell);
                    }
                    list.add(cells);
                }
            }
            workbook.close();
        }
        return list;
    }
    private static void checkFile(MultipartFile file) throws IOException{
        //判断文件是否存在
        if(null == file){
            throw new FileNotFoundException("文件不存在！");
        }
        //获得文件名
        String fileName = file.getOriginalFilename();
        //判断文件是否是excel文件
        if(!fileName.endsWith(xls) && !fileName.endsWith(xlsx)){
            throw new IOException(fileName + "不是excel文件");
        }
    }

    private static Workbook getWorkBook(MultipartFile file) {
        //获得文件名
        String fileName = file.getOriginalFilename();
        //创建Workbook工作薄对象，表示整个excel
        Workbook workbook = null;
        try {
            //获取excel文件的io流
            InputStream is = file.getInputStream();
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if(fileName.endsWith(xls)){
                //2003
                workbook = new HSSFWorkbook(is);
            }else if(fileName.endsWith(xlsx)){
                //2007
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workbook;
    }

    private static Object getCellValue(Cell cell){
        Object cellValue = "";
        if(cell == null){
            return cellValue;
        }
        //把数字当成String来读，避免出现1读成1.0的情况
        if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
        //判断数据的类型
        switch (cell.getCellType()){
            case Cell.CELL_TYPE_NUMERIC: //数字
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    cellValue = DateFormatUtils.format(date, "yyyy-MM-dd");
                    System.out.println("日期：" + cellValue);
                } else {
                    cellValue = cell.getNumericCellValue();
                    DecimalFormat df = new DecimalFormat("0");
                    cellValue = df.format(cellValue );
                }
                break;
            case Cell.CELL_TYPE_STRING: //字符串
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN: //Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA: //公式
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case Cell.CELL_TYPE_BLANK: //空值
                cellValue = "";
                break;
            case Cell.CELL_TYPE_ERROR: //故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }




	/**
	 * 导出excel
	 * 
	 * @param fileName
	 * @param headers
	 * @param datas
	 * @param response
	 */
	public static void exportExcel(String fileName, String[] headers, List<Object[]> datas,
			HttpServletResponse response) {
		Workbook workbook = getWorkbook(headers, datas);
		if (workbook != null) {
			ByteArrayOutputStream byteArrayOutputStream = null;
			try {
				byteArrayOutputStream = new ByteArrayOutputStream();
				workbook.write(byteArrayOutputStream);

				String suffix = ".xls";
				response.setContentType("application/vnd.ms-excel;charset=utf-8");
				response.setHeader("Content-Disposition",
						"attachment;filename=" + new String((fileName + suffix).getBytes(), "iso-8859-1"));

				OutputStream outputStream = response.getOutputStream();
				outputStream.write(byteArrayOutputStream.toByteArray());
				outputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (byteArrayOutputStream != null) {
						byteArrayOutputStream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 
	 * @param headers
	 *            列头
	 * @param datas
	 *            数据
	 * @return
	 */
	public static Workbook getWorkbook(String[] headers, List<Object[]> datas) {
		Workbook workbook = new HSSFWorkbook();

		Sheet sheet = workbook.createSheet();
		Row row = null;
		Cell cell = null;
		CellStyle style = workbook.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER_SELECTION);

		Font font = workbook.createFont();

		int line = 0, maxColumn = 0;
		if (headers != null && headers.length > 0) {// 设置列头
			row = sheet.createRow(line++);
			row.setHeightInPoints(23);
			font.setBold(true);
			font.setFontHeightInPoints((short) 13);
			style.setFont(font);

			maxColumn = headers.length;
			for (int i = 0; i < maxColumn; i++) {
				cell = row.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(style);
			}
		}

		if (datas != null && datas.size() > 0) {// 渲染数据
			for (int index = 0, size = datas.size(); index < size; index++) {
				Object[] data = datas.get(index);
				if (data != null && data.length > 0) {
					row = sheet.createRow(line++);
					row.setHeightInPoints(20);

					int length = data.length;
					if (length > maxColumn) {
						maxColumn = length;
					}

					for (int i = 0; i < length; i++) {
						cell = row.createCell(i);
						cell.setCellValue(data[i] == null ? null : data[i].toString());
					}
				}
			}
		}

		for (int i = 0; i < maxColumn; i++) {
			sheet.autoSizeColumn(i);
		}

		return workbook;
	}
}
