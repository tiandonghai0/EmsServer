package com.shmet.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StatisticsResponseBean {
    private String tagcode;
    private List<String> times;
    private List<String> vals;
}
