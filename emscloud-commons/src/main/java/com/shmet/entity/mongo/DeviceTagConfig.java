package com.shmet.entity.mongo;

import java.util.List;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import com.shmet.bean.ComputeFormulaBean;

/**
 * 设备点位配置信息
 *
 */
@Document(collection = "deviceTagConfig")
@CompoundIndexes({
        @CompoundIndex(name = "devicemodel_tagcode_idx", def = "{'deviceModel': 1, 'tagCode': 1}", unique = true)
})
public class DeviceTagConfig {
    @Id
    private String id;

    // 设备型号，每种设备型号对应一组点位配置
    @NotNull
    private String deviceModel;

    // 设备型号，每种设备型号对应一组点位配置
    @NotNull
    private String modelName;

    // tagCode作为采集点位ID，在页面中是以combobox形式给用户选择，不允许用户自定义名称
    @NotNull
    private String tagCode;

    // 点位名称可以让用户起名
    @NotNull
    private String tagName;

    // 0:正常点位，1:异常点位，2:未配置点位
    @NotNull
    private int tagType;

    // 用于存储实时数据的数据结构类型，如Double. String. Float. Int. Long. ByteArray等
    @NotNull
    private String dataType;

    @NotNull
    private boolean hasStatistics;

    // 异常点位在正常状态下的取值，用[,]隔开
    @NotNull
    private String errorTagNormalVals;

    @NotNull
    private int errorLevel = -1;

    // 0:电量(或和电量统计计量方式类似的点位)，1:功率（或和功率统计计量方式类似的点位）
    @NotNull
    private int statisticsType;

    @NotNull
    private List<String> statisticsMethods;

    @NotNull
    private String realRecordMethod;

    @NotNull
    private String faultRecordMethod;

    // 采集频率，以秒为单位
    @NotNull
    private int frequency;

    // 该点位是否需要由其它点位计算得到
    @NotNull
    private boolean needCompute;

    // 在页面中，用户可通过选择其它点位名称、计算符号来构建计算公式
    // 注意，需要计算的点位，作为计算因子的其它点位必须同时采集得到
    @NotNull
    private ComputeFormulaBean computeFormula;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getTagCode() {
        return tagCode;
    }

    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getTagType() {
        return tagType;
    }

    public void setTagType(int tagType) {
        this.tagType = tagType;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public boolean isHasStatistics() {
        return hasStatistics;
    }

    public void setHasStatistics(boolean hasStatistics) {
        this.hasStatistics = hasStatistics;
    }

    public int getStatisticsType() {
        return statisticsType;
    }

    public void setStatisticsType(int statisticsType) {
        this.statisticsType = statisticsType;
    }

    public String getErrorTagNormalVals() {
        return errorTagNormalVals;
    }

    public void setErrorTagNormalVals(String errorTagNormalVals) {
        this.errorTagNormalVals = errorTagNormalVals;
    }

    public int getErrorLevel() {
        return errorLevel;
    }

    public void setErrorLevel(int errorLevel) {
        this.errorLevel = errorLevel;
    }

    public List<String> getStatisticsMethods() {
        return statisticsMethods;
    }

    public void setStatisticsMethods(List<String> statisticsMethods) {
        this.statisticsMethods = statisticsMethods;
    }

    public String getRealRecordMethod() {
        return realRecordMethod;
    }

    public void setRealRecordMethod(String realRecordMethod) {
        this.realRecordMethod = realRecordMethod;
    }

    public String getFaultRecordMethod() {
        return faultRecordMethod;
    }

    public void setFaultRecordMethod(String faultRecordMethod) {
        this.faultRecordMethod = faultRecordMethod;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public boolean isNeedCompute() {
        return needCompute;
    }

    public void setNeedCompute(boolean needCompute) {
        this.needCompute = needCompute;
    }

    public ComputeFormulaBean getComputeFormula() {
        return computeFormula;
    }

    public void setComputeFormula(ComputeFormulaBean computeFormula) {
        this.computeFormula = computeFormula;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

}
