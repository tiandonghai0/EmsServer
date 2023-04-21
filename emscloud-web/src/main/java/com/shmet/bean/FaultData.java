package com.shmet.bean;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "faultData")
@CompoundIndexes(@CompoundIndex(name = "deviceId_time_index", def = "{'subprojectId': 1,'deviceId': 1, 'createTime': -1}"))
public class FaultData {
  private String deviceId;
  private Long subprojectId;
  private String deviceName;
  private Integer modelType;
  private String tagcode;
  private String tagcodeName;
  private String dataValue;
  private String content;
  private Long createTime;
  private Integer faultType;
}