package com.shmet.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "filepath.logo")
public class FileLogoProp {
  private String filePrefix;
  private String dbPrefix;
  //文件大小
  private Long maxSize;
  private List<String> fileExts;
}
