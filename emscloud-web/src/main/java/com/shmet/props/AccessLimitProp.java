package com.shmet.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "access.limit")
public class AccessLimitProp {
  private List<String> urls;
}
