package com.shmet.bean;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
public class StatisticsFormBean {
    @NotNull(message = "deviceNo不能为空")
    private List<Long> deviceNos;
    @NotNull(message = "tagcodes不能为空")
    private List<String> tagcodes;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "日期格式不合法")
    private String date;
}
