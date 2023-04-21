package com.shmet.handler.v2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shmet.ArithUtil;
import com.shmet.helper.redis.RedisUtil;
import com.shmet.utils.DateUtils;
import com.shmet.bean.RealDataItem;
import com.shmet.dao.AdConnrecordinfoMapper;
import com.shmet.entity.dto.ResultSearch;
import com.shmet.entity.mysql.gen.AdConnrecordinfo;
import com.shmet.vo.req.AdCreateReq;
import com.shmet.vo.req.AdQueryReq;
import com.shmet.vo.req.AdUpdateReq;
import com.shmet.vo.res.AdResponseVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

/**
 * @author
 */
@Service
public class AdConnrecordinfoService {

  //*********************************** Static Field ****************************
  private static final Map<String, String> map = new ConcurrentHashMap<>();

  private static final Logger logger = LoggerFactory.getLogger(AdConnrecordinfoService.class);

  static {
    map.put("BZ222000601", "202020010010003");
    map.put("BZ222000602", "202020010010004");
    map.put("BZ222000501", "202020010010006");
    map.put("BZ222000701", "202020010010009");
    map.put("BZ222000702", "202020010010010");
    map.put("BZ222000801", "202020010010013");
    map.put("BZ222000802", "202020010010014");
  }
  //*********************************** Static Field ****************************

  @Resource
  private AdConnrecordinfoMapper adConnrecordinfoMapper;

  @Autowired
  private CommonService commonService;

  @Resource
  private ObjectMapper objectMapper;

  @Autowired
  private RedisUtil redisUtil;

  //分页条件查询列表
  public ResultSearch pageConditionList(AdQueryReq req) {
    Page<AdConnrecordinfo> pageReq = new Page<>(req.getPageNum(), req.getPageSize());
    String shipName = req.getShipName();
    String berthTime = req.getBerthTime();
    QueryWrapper<AdConnrecordinfo> queryWrapper = new QueryWrapper<>();

    if (StringUtils.isNotBlank(shipName)) {
      queryWrapper.likeRight("ShipName", shipName);
    }
    if (StringUtils.isNotBlank(berthTime)) {
      queryWrapper.likeRight("BerthTime", berthTime);
    }
    queryWrapper.orderByDesc("BerthTime");
    IPage<AdConnrecordinfo> page = adConnrecordinfoMapper.selectPage(pageReq, queryWrapper);
    List<AdConnrecordinfo> records = page.getRecords();
    List<AdResponseVo> vos = records.stream().map(o -> {
      AdResponseVo vo = new AdResponseVo();
      BeanUtils.copyProperties(o, vo);
      return vo;
    }).collect(toList());
    ResultSearch rs = ResultSearch.getSuccessResultInfo(vos, "查询成功");
    rs.setPageNo(req.getPageNum());
    rs.setPageSize(req.getPageSize());
    rs.setTotalCount(page.getTotal());
    rs.setPageCount(page.getPages());

    return rs;
  }

  /**
   * 录入连船信息
   */
  public int saveAdConnrecordinfo(AdCreateReq req) {
    AdConnrecordinfo info = new AdConnrecordinfo();
    BeanUtils.copyProperties(req, info);
    info.setBerthStatus("充电中");
    info.setDate(LocalDate.now().toString());
    //辅机额定功率 写死 380
    info.setAuxiliaryPower(BigDecimal.valueOf(380));
//    commonService.cursorItem()
    // 模拟 真实情况应该是读取点位数据 受电电压:  受电频率:
    //info.setRecvVoltage(BigDecimal.valueOf(30));
    BigDecimal rv = calcRecv(req, "D_AVU");
    if (rv != null) {
      info.setRecvVoltage(rv);
    }
    //info.setRecvFrequency(BigDecimal.valueOf(60));
    BigDecimal rf = calcRecv(req, "D_FR");
    if (rf != null) {
      info.setRecvFrequency(rf);
    }
    //设置开始用电时间
    info.setStartTime(req.getBerthTime());
    // 开始时 进线电量 出线电量
    info.setStartInletElectricity(BigDecimal.valueOf(1000));
    info.setStartExportElectricity(BigDecimal.valueOf(1000));
    return adConnrecordinfoMapper.insert(info);
  }

  private BigDecimal calcRecv(AdCreateReq req, String tagcode) {
    String dno = map.get(req.getDeviceID());
    if (StringUtils.isNotBlank(dno)) {
      //String itemStr = commonService.cursorItem(dno, tagcode);
      String itemStr = (String) redisUtil.hget("RealDataRedisDao.saveRealDataCache", dno + "." + tagcode, -1);
      if (StringUtils.isNotBlank(itemStr)) {
        RealDataItem data = null;
        try {
          data = objectMapper.readValue(itemStr, RealDataItem.class);
        } catch (IOException e) {
          logger.error("JSON转换出错");
        }
        if (data != null) {
          return BigDecimal.valueOf(Double.parseDouble(String.valueOf(data.getData() == null ? "" : data.getData())));
        }
        return null;
      }
    }
    return null;
  }

  //结束用电 更新 离泊时间 用电量 结束电量
  @Transactional
  public int endElec(Integer id) {
    AdConnrecordinfo adConnrecordinfo = adConnrecordinfoMapper.selectById(id);
    if (Objects.nonNull(adConnrecordinfo)) {
      adConnrecordinfo.setEndInletElectricity(BigDecimal.valueOf(1200));
      adConnrecordinfo.setEndExportElectricity(BigDecimal.valueOf(1200));
      if (adConnrecordinfo.getEndInletElectricity() != null && adConnrecordinfo.getStartInletElectricity() != null) {
        double end = adConnrecordinfo.getEndInletElectricity().doubleValue();
        double start = adConnrecordinfo.getStartInletElectricity().doubleValue();
        adConnrecordinfo.setUseElectricity(BigDecimal.valueOf(ArithUtil.sub(end, start)));
      }
      adConnrecordinfo.setDepartureTime(DateUtils.getCurrentTimeStr(LocalDateTime.now()));
      adConnrecordinfo.setEndTime(DateUtils.getCurrentTimeStr(LocalDateTime.now()));
      adConnrecordinfo.setBerthStatus("结束用电");
      return adConnrecordinfoMapper.updateById(adConnrecordinfo);
    }

    return 0;
  }

  //只更新 靠泊时间 跟 离泊时间
  public int update(AdUpdateReq req) {
    AdConnrecordinfo info = adConnrecordinfoMapper.selectById(req.getId());
    if (info != null) {
      //更新靠泊时间 更新的靠泊时间必须 早于 开始用电时间
      if (DateUtils.compareTime(req.getBerthTime(), info.getStartTime()) <= 0) {
        info.setBerthTime(req.getBerthTime());
      } else {
        throw new UnsupportedOperationException("待更新的靠泊时间不能晚于开始用电时间");
      }
      if (StringUtils.isNotBlank(info.getEndTime())) {
        if (DateUtils.compareTime(req.getDepartureTime(), info.getEndTime()) >= 0) {
          //更新离泊时间
          info.setDepartureTime(req.getDepartureTime());
        } else {
          throw new UnsupportedOperationException("待更新的离泊时间不能早于结束用电时间");
        }
      } else {
        throw new UnsupportedOperationException("结束用电时间为空 请先结束用电 在进行更改 离泊时间");
      }
      return adConnrecordinfoMapper.updateById(info);
    }
    return -1;
  }
}
