package com.shmet.helper;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

@Component
public class ExcelUtils {

    private static String reportFilePath = "C:/Program Files/apache-tomcat-8.0.53/webapps/file";

    private static String reportFile = "/file/";

    //因为静态不能获取@Value
    private static String website = "http://www.jz-energy.cn";

    /**
     * 创建excel
     *
     * @param datas      数据
     * @param sheetName  工作簿名称
     * @param title      图表标题
     * @param type       横向名称
     * @param unit       竖向单位
     * @param fontSize   字体大小
     * @param width      图表宽度
     * @param height     图表高度
     * @param filePath   保存文件路径
     * @param fileName   保存文件名称
     * @param reportType 报表类型，1折线2D、2折线3D、3柱形
     * @return 文件地址
     * @throws IOException
     */
    public static String createExcel(Map<String, List<Map<String, Double>>> datas, String sheetName, String title, String type, String unit, int fontSize, int width, int height, String filePath, String fileName, int reportType) throws IOException {

        System.out.println("开始执行excel生成");
        // excel2003工作表
        HSSFWorkbook wb = new HSSFWorkbook();
        if (sheetName == null) {
            sheetName = "";
        }
        // 创建工作表
        HSSFSheet sheet = wb.createSheet(sheetName + "图表");
        // 创建字节输出流
        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        //如 果不使用Font,中文将显示不出来
        Font font = new Font("新宋体", Font.BOLD, fontSize);

//        // 创建数据
//        Map<String, Map<String, Double>> datas =new HashMap<String, Map<String,Double>>();
//
//        Map<String, Double> map1=new HashMap<String, Double>();
//        Map<String, Double> map2=new HashMap<String, Double>();
//        Map<String, Double> map3=new HashMap<String, Double>();
//        Map<String, Double> map4=new HashMap<String, Double>();
//        Map<String, Double> map5=new HashMap<String, Double>();
//
//        map1.put("故障数量", (double) 20);
//        map2.put("故障数量", (double) 30);
//        map3.put("故障数量", (double) 25);
//        map4.put("故障数量", (double) 50);
//        map5.put("故障数量", (double) 40);
//
//        //压入数据
//        datas.put("一月", map1);
//        datas.put("二月", map2);
//        datas.put("三月", map3);
//        datas.put("四月", map4);
//        datas.put("五月", map5);


        JFreeChart chart = createPort(title, datas, type, unit, font, reportType, fontSize);

        // 读取chart信息至字节输出流
        ChartUtilities.writeChartAsPNG(byteArrayOut, chart, width, height);
        // 画图的顶级管理器，一个sheet只能获取一个（一定要注意这点）
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
        // anchor主要用于设置图片的属性
        HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 0, 0, (short) 3, (short) 1, (short) 30, (short) 35);
        anchor.setAnchorType(ClientAnchor.AnchorType.DONT_MOVE_AND_RESIZE);

        // .DONT_MOVE_AND_RESIZE);
        // 插入图片
        patriarch.createPicture(anchor, wb.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG));

        wb = exportExcel(datas, wb, sheetName + "报表");

        if (filePath == null || filePath.length() == 0) {
            filePath = reportFilePath;
        }

        // excel2003后缀
        FileOutputStream fileOut = new FileOutputStream(filePath + "\\" + fileName);
        wb.write(fileOut);
        fileOut.close();

        return website + reportFile + fileName;
    }

    public static JFreeChart createPort(String title, Map<String, List<Map<String, Double>>> datas, String type, String unit, Font font, int reportType, int fontSize) {
        try {
            DefaultCategoryDataset ds = new DefaultCategoryDataset();
            Set<Map.Entry<String, List<Map<String, Double>>>> set1 = datas.entrySet();
            Iterator iterator1 = set1.iterator();
            Iterator iterator2;
            List<Map<String, Double>> map;
            Set<Map.Entry<String, Double>> set2;
            Map.Entry entry1;
            Map.Entry entry2;
            while (iterator1.hasNext()) {
                entry1 = (Map.Entry) iterator1.next();
                map = (List<Map<String, Double>>) entry1.getValue();
                for (Map<String, Double> m : map
                ) {
                    set2 = m.entrySet();
                    iterator2 = set2.iterator();
                    while (iterator2.hasNext()) {
                        entry2 = (Map.Entry) iterator2.next();
                        ds.setValue(Double.parseDouble(entry2.getValue().toString()), entry2.getKey().toString(), entry1.getKey().toString());
                    }
                }

            }


            //创建折线图,折线图分水平显示和垂直显示两种
            // //2D折线图
            JFreeChart chart = ChartFactory.createLineChart(title, type, unit, ds, PlotOrientation.VERTICAL, true, true, true);


            switch (reportType) {
                case 1:
                    //2D折线图
                    chart = ChartFactory.createLineChart(title, type, unit, ds, PlotOrientation.VERTICAL, true, true, true);
                    break;
                case 2:
                    //3D折线图
                    chart = ChartFactory.createLineChart3D(title, type, unit, ds, PlotOrientation.VERTICAL, true, true, false);
                    break;
                case 3:
                    //创建柱状图,柱状图分水平显示和垂直显示两种
                    chart = ChartFactory.createBarChart(title, type, unit, ds, PlotOrientation.VERTICAL, true, true, true);
                    break;
                default:
                    //2D折线图
                    chart = ChartFactory.createLineChart(title, type, unit, ds, PlotOrientation.VERTICAL, true, true, true);

                    break;
            }

            //设置整个图片的标题字体
            chart.getTitle().setFont(font);

            //设置提示条字体
            font = new Font("宋体", Font.BOLD, fontSize + 1);
            chart.getLegend().setItemFont(font);

            //得到绘图区
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            //得到绘图区的域轴(横轴),设置标签的字体
            plot.getDomainAxis().setLabelFont(font);

            //设置横轴标签项字体
            plot.getDomainAxis().setTickLabelFont(font);

            //设置范围轴(纵轴)字体
            font = new Font("宋体", Font.BOLD, fontSize + 4);
            plot.getRangeAxis().setLabelFont(font);
//            plot.setForegroundAlpha(1.0f);


            CategoryAxis categoryAxis = plot.getDomainAxis();
            // 横轴上的 Lable 90度倾斜
            categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);

            return chart;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static HSSFWorkbook exportExcel(Map<String, List<Map<String, Double>>> datas, HSSFWorkbook hssfWorkbook, String sheetName) {


        System.out.println("datas:" + JsonUtils.objToString(datas));
        List<String> rowTitle = new ArrayList<>();//所有统计行，最终使用去重的 rowTitles
        if (datas != null && datas.size() > 0) {
            String[] title = new String[datas.size() + 1];
            title[0] = "-";


            Set<Map.Entry<String, List<Map<String, Double>>>> set1 = datas.entrySet();
            Iterator iterator1 = set1.iterator();
            Iterator iterator2;
            List<Map<String, Double>> map;
            Set<Map.Entry<String, Double>> set2;
            Map.Entry entry1;
            Map.Entry entry2;


            //为了获取总行数，提前获取行数，好组装二维数组
            while (iterator1.hasNext()) {
                int i = 0;

                entry1 = (Map.Entry) iterator1.next();

                map = (List<Map<String, Double>>) entry1.getValue();
                for (Map<String, Double> m : map
                ) {
                    set2 = m.entrySet();
                    iterator2 = set2.iterator();
                    while (iterator2.hasNext()) {
                        int n = 0;
                        entry2 = (Map.Entry) iterator2.next();
                        rowTitle.add(entry2.getKey().toString());
                        n++;
                    }
                }

            }
            //统计点去重就是行
            String[] rowTitles = distinct(rowTitle.toArray(new String[rowTitle.size()]));
            String[][] content = new String[rowTitles.length][title.length];
            List<Object> i = new ArrayList<>();
            List<Object> n = new ArrayList<>();
            iterator1 = set1.iterator();
            //开始写入数据
            while (iterator1.hasNext()) {

                entry1 = (Map.Entry) iterator1.next();
                title[i.size() + 1] = entry1.getKey().toString();
                //System.out.println("i:"+i+",key:"+entry1.getKey().toString());
                map = (List<Map<String, Double>>) entry1.getValue();
                for (Map<String, Double> m : map
                ) {
                    set2 = m.entrySet();
                    iterator2 = set2.iterator();
                    while (iterator2.hasNext()) {

                        entry2 = (Map.Entry) iterator2.next();

                        for (int j = 0; j < rowTitles.length; j++) {
                            if (entry2.getKey().toString().equals(rowTitles[j])) {
                                content[j][i.size() + 1] = entry2.getValue().toString();
                            }
                        }


                        n.add(entry2.getKey());
                    }
                }
                i.add(entry1.getKey());
            }


            //写纵向名称,没行的第一列写入横向名称
            for (int j = 0; j < rowTitles.length; j++) {
                content[j][0] = rowTitles[j];
            }

//            System.out.println("title:"+JsonUtils.objToString(title));
//            System.out.println("content:"+JsonUtils.objToString(content));
//            System.out.println("rowTitles:"+JsonUtils.objToString(rowTitles));
            // 导出Excel
            try {
                return getHSSFWorkbook(sheetName, title, content, hssfWorkbook);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        return null;
    }

    public static HSSFWorkbook getHSSFWorkbook(String sheetName, String[] title, String[][] values, HSSFWorkbook workbook) {
        // 创建一个HSSFWorkbook，对应一个Excel文件
        if (workbook == null) {
            workbook = new HSSFWorkbook();
        }
        // 在workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = workbook.createSheet(sheetName);
        // 在sheet中添加表头第0行
        HSSFRow row = sheet.createRow(0);
        // 创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        //cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        // 声明列对象
        HSSFCell cell = null;
        // 创建标题
        for (int i = 0; i < title.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(cellStyle);
        }
        // 创建内容
        for (int i = 0; i < values.length; i++) {
            row = sheet.createRow(i + 1);
            for (int j = 0; j < values[i].length; j++) {
                // 将内容按顺序赋给对应的列对象
                row.createCell(j).setCellValue(values[i][j]);
            }
        }
        return workbook;

    }

    public static String[] distinct(String[] arr) {
        List<String> strings = new ArrayList<>();
        for (String s : arr) {
            if (s != null) {
                boolean isReapt = false;
                for (String ss : strings) {
                    if (ss != null) {
                        if (s.equals(ss)) {
                            isReapt = true;
                        }
                    }
                }
                if (!isReapt) {
                    strings.add(s);
                }
            }
        }
        return strings.toArray(new String[strings.size()]);
    }

}
