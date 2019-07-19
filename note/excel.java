package com.yukens.utils;

import com.yukens.dto.ExcelReport;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

/**
 * @Description:利用poi，生成excel表格
 */
@Component
public class ExcelFormDB {

//    private static String BASE_PATH = "/ccicall/report/";
    private static String BASE_PATH = "E:/mat/";

    // 导出的Excel表的sheet名字设置为
    public static String tables = "报表信息";

    public static String excelReport(List<ExcelReport> excelReportList) {
        FileOutputStream fout = null;
        String startTime = DateUtils.parseLocalDateTimeToString(DateUtils.getStartTimeOfLastWeek());
        String endTime = DateUtils.parseLocalDateTimeToString(DateUtils.getEndTimeOfLastWeek());
        String filePath = BASE_PATH + DateUtils.parseLocalDateTimeToString2(LocalDateTime.now()) + File.separator + startTime + "-" + endTime + ".xlsx";
        FileUtil.saveUploadFile(filePath);
        try {
            SXSSFWorkbook workbook = new SXSSFWorkbook(-1);
            //创建一个sheet
            SXSSFSheet sheet = workbook.createSheet(tables);
            //设置样式
            CellStyle style =workbook.createCellStyle();
            Font font =workbook.createFont();
            //加粗
            font.setBold(true);
            font.setFontName("宋体");
            //设置字号
            font.setFontHeightInPoints((short) 15);
            //设置字体颜色
            font.setColor(IndexedColors.BLACK.getIndex());
            style.setFont(font);
            //设置前景色
            style.setFillForegroundColor(IndexedColors.RED.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            //创建表头
            //创建第一行,自动适应列宽

            sheet.trackAllColumnsForAutoSizing();
            SXSSFRow row = sheet.createRow(0);
            row.createCell(0).setCellValue("询价单号");
            row.createCell(1).setCellValue("报案号");
            row.createCell(2).setCellValue("询价接收时间");
            row.createCell(3).setCellValue("报价提交时间");
            row.createCell(4).setCellValue("定损要求报价时间");
            row.createCell(5).setCellValue("报价备注");
            row.createCell(6).setCellValue("下单时间");
            row.createCell(7).setCellValue("交易确认时间");
            row.createCell(8).setCellValue("品牌名称");
            row.createCell(9).setCellValue("配件标准代码");
            row.createCell(10).setCellValue("配件标准名称");
            row.createCell(11).setCellValue("换件数量");
            row.createCell(12).setCellValue("询价参考价");
            row.createCell(13).setCellValue("询价参考价合计");
            row.createCell(14).setCellValue("询价配件品质");
            row.createCell(15).setCellValue("汽配商报价");
            row.createCell(16).setCellValue("汽配商报价总价");
            row.createCell(17).setCellValue("省");
            row.createCell(18).setCellValue("市");
            row.createCell(19).setCellValue("区");
            row.createCell(20).setCellValue("修理厂详细地址");
            row.createCell(21).setCellValue("汽配商名称");
            row.createCell(22).setCellValue("汽配商编号");
            row.createCell(23).setCellValue("定损人员名称");
            row.createCell(24).setCellValue("定损人员电话");
            row.createCell(25).setCellValue("是否直供");
            row.createCell(26).setCellValue("订单状态");
            //给表头设置样式。
            for(int i=0;i<27;i++)
            {
                //给每一个单元格设置样式
                row.getCell(i).setCellStyle(style);
            }
            //总行数
            int rowNum = 1;
            for (ExcelReport excelReport : excelReportList) {
                row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(excelReport.getInqueryNo());
                row.createCell(1).setCellValue(excelReport.getInsuranceNo());
                row.createCell(2).setCellValue(excelReport.getInqueryTime());
                row.createCell(3).setCellValue(excelReport.getQuotationTime());
                row.createCell(4).setCellValue(excelReport.getDingsunTime());
                row.createCell(5).setCellValue(excelReport.getRemark());
                row.createCell(6).setCellValue(excelReport.getOrderTime());
                row.createCell(7).setCellValue(excelReport.getResultTime());
                row.createCell(8).setCellValue(excelReport.getBrandName());
                row.createCell(9).setCellValue(excelReport.getPartCode());
                row.createCell(10).setCellValue(excelReport.getPartName());
                row.createCell(11).setCellValue(excelReport.getPartNum());
                row.createCell(12).setCellValue(excelReport.getPartPrice());
                row.createCell(13).setCellValue(excelReport.getSumPartPrice());
                row.createCell(14).setCellValue(excelReport.getPartType());
                row.createCell(15).setCellValue(excelReport.getQuotationPrice());
                row.createCell(16).setCellValue(excelReport.getSumQuotationPrice());
                row.createCell(17).setCellValue(excelReport.getProName());
                row.createCell(18).setCellValue(excelReport.getCityName());
                row.createCell(19).setCellValue(excelReport.getDistinct());
                row.createCell(20).setCellValue(excelReport.getDetailAddress());
                row.createCell(21).setCellValue(excelReport.getSellerName());
                row.createCell(22).setCellValue(excelReport.getSellerId());
                row.createCell(23).setCellValue(excelReport.getConnectName());
                row.createCell(24).setCellValue(excelReport.getConnectPhone());
                row.createCell(25).setCellValue(excelReport.getOrderType());
                row.createCell(26).setCellValue(excelReport.getOrderStatus());
                rowNum++;
            }
            //自动适应列宽
            for (int k = 0; k < 27; k++) {
                sheet.autoSizeColumn(k);
            }

            //合并单元格
            //起始
            int firstclon = 1;
            //结束
            int lastClon = 0;
            for (int i = 0; i < 1; i++) {
                String str1 = sheet.getRow(1).getCell(i).getStringCellValue();
                //获取第一行数据
                for (int j = 2; j <= excelReportList.size(); j++) {
                    //i表示获取第几列的数据，j表示第几行
                    if (!(ObjectUtils.isEmpty(sheet.getRow(j).getCell(i)))) {
                        String str2 = sheet.getRow(j).getCell(i).getStringCellValue();
                        if (str2.equals(str1)) {
                            lastClon = j;
                            //循环结束
                            if (j == excelReportList.size()) {
                                CellRangeAddress region = new CellRangeAddress(firstclon, lastClon, i, i);
                                sheet.addMergedRegion(region);
                            }
                        } else {
                            if (lastClon - firstclon > 0) {
                                CellRangeAddress region = new CellRangeAddress(firstclon, lastClon, i, i);
                                sheet.addMergedRegion(region);
                                //合并玩之后
                                firstclon = j;
                                str1 = str2;
                            } else {
                                firstclon = j;
                                str1 = str2;
                            }
                        }
                    }

                }
                //一次循环完之后要将位置初始化
                firstclon = 1;
                lastClon = 0;
            }
            try {
                fout = new FileOutputStream(filePath);
                workbook.write(fout);
                fout.flush();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } finally {
            try {
                if (fout != null) {
                    fout.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePath;
    }
}






