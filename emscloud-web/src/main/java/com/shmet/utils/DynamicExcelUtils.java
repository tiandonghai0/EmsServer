package com.shmet.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public final class DynamicExcelUtils {

  private static final String DEFAULT_SHEET_NAME = "sheet1";

  /**
   * 动态导出文件
   *
   * @param headColumnMap 有序列头部
   * @param dataList      数据体
   */
  public static byte[] exportExcelFile(LinkedHashMap<String, String> headColumnMap, List<Map<String, Object>> dataList) {
    //获取列名称
    List<List<String>> excelHead = new ArrayList<>();
    if (MapUtils.isNotEmpty(headColumnMap)) {
      //key为匹配符,value为列名,如果多级列名用逗号隔开
      headColumnMap.forEach((key, value) -> excelHead.add(Lists.newArrayList(value.split(","))));
    }
    List<List<Object>> excelRows = new ArrayList<>();
    if (MapUtils.isNotEmpty(headColumnMap) && CollectionUtils.isNotEmpty(dataList)) {
      for (Map<String, Object> dataMap : dataList) {
        List<Object> rows = new ArrayList<>();
        headColumnMap.forEach((key, value) -> {
          if (dataMap.containsKey(key)) {
            Object data = dataMap.get(key);
            if (data instanceof List) {
              List<?> list = (List<?>) data;
              rows.add(list.get(0));
            } else {
              rows.add(data);
            }
          }
        });
        excelRows.add(rows);
      }
    }
    return createExcelFile(excelHead, excelRows);
  }

  /**
   * 生成文件
   *
   * @param excelHead excelHead
   * @param excelRows excelRows
   */
  private static byte[] createExcelFile(List<List<String>> excelHead, List<List<Object>> excelRows) {
    try {
      if (CollectionUtils.isNotEmpty(excelHead)) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream)
            .registerWriteHandler(new SimpleColumnWidthStyleStrategy(18))
            .head(excelHead)
            .sheet(DEFAULT_SHEET_NAME)
            .doWrite(excelRows);
        return outputStream.toByteArray();
      }
    } catch (Exception e) {
      log.error("动态生成excel文件失败,headColumns: " + JSONArray.toJSONString(excelHead) + ",excelRows: " + JSONArray.toJSONString(excelRows), e);
    }
    return null;
  }
}
