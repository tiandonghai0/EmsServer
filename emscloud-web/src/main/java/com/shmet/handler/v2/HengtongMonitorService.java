package com.shmet.handler.v2;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shmet.bean.AcconeStatusBean;
import com.shmet.bean.RealDataItem;
import com.shmet.dao.DeviceMapper;
import com.shmet.dao.mongodb.ShorePowerLogDataDao;
import com.shmet.entity.dto.ResultSearch;
import com.shmet.entity.mongo.ShorePowerLogData;
import com.shmet.entity.mysql.gen.Device;
import com.shmet.exception.EmsBisException;
import com.shmet.vo.HengtongMonitorVo;
import com.shmet.vo.req.HengtongMonitorReq;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HengtongMonitorService {

  public static final String REAL_DATA = "RealDataRedisDao.saveRealDataCache";
  public static final String STAUS_DATA = "TAcconeDao.saveAcconeStatus";

  private final DeviceMapper deviceMapper;
  private final RedisTemplate<String, Object> redisTemplate;
  private final  ShorePowerLogDataDao shorePowerLogDataDao;

  public ResultSearch pageQuery(HengtongMonitorReq req) {
    Page<Device> page = new Page<>(req.getPageNum(), req.getPageSize());
    LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
    //queryWrapper.likeRight(Device::getSubProjectId, 20200009L);
    queryWrapper.and((wrapper)->{
      wrapper.likeRight(Device::getSubProjectId, 20200009L)
              .or().in(Device::getSubProjectId,20210901L,20210902L);
    });

    if (StringUtils.isNotBlank(req.getDeviceNo())) {
      queryWrapper.like(Device::getDeviceNo, req.getDeviceNo());
    }
    if (StringUtils.isNotBlank(req.getDeviceName())) {
      queryWrapper.like(Device::getDeviceName, req.getDeviceName());
    }
    if (StringUtils.isNotBlank(req.getEmuId())) {
      queryWrapper.like(Device::getAcconeId, req.getEmuId());
    }
    queryWrapper.orderByDesc(Device::getCreateDate);
    IPage<Device> ipages = deviceMapper.selectPage(page, queryWrapper);

    List<HengtongMonitorVo> htVos = new ArrayList<>();

    for (Device device : ipages.getRecords()) {
      Long dno = device.getDeviceNo();
      Long emuId = device.getAcconeId();
      HengtongMonitorVo vo = HengtongMonitorVo.buildVo(device);

      //总电能
      Object elesEnergy = redisTemplate.opsForHash().get(REAL_DATA, dno + ".TPAE");
      if (elesEnergy != null) {
        RealDataItem item = (RealDataItem) elesEnergy;
        Object data = item.getData();
        if (data instanceof List) {
          List<?> list = (List<?>) data;
          vo.setElecEnergy(String.valueOf(list.get(0)));
        } else {
          vo.setElecEnergy(String.valueOf(data));
        }
        //最新数据时间
        vo.setLatestDataTime(item.getDateTime());
      }
      //功率
      Object p = redisTemplate.opsForHash().get(REAL_DATA, dno + ".CAP");
      if (p != null) {
        RealDataItem item = (RealDataItem) p;
        vo.setP(String.valueOf(item.getData()));
      }
      //emu状态 最后登录时间
      Object statusBean = redisTemplate.opsForHash().get(STAUS_DATA, String.valueOf(emuId));
      if (statusBean != null) {
        AcconeStatusBean status = (AcconeStatusBean) statusBean;
        vo.setLoginLastTime(String.valueOf(status.getLastLoginTime()));
        vo.setEmuStatus(String.valueOf(status.getConnectedStatus()));
      }

      ShorePowerLogData logData = shorePowerLogDataDao.findLastByAcconeDevice(Long.parseLong(vo.getEmuId()), Integer.parseInt(vo.getDeviceId()));
      if (logData == null) {
        vo.setDeviceStatus(0);
      }else{
        vo.setDeviceStatus(logData.getSsType());
      }

      htVos.add(vo);
    }
    ResultSearch res = ResultSearch.getSuccessResultInfo(htVos, "查询成功");
    res.setPageNo(req.getPageNum());
    res.setPageSize(req.getPageSize());
    res.setTotalCount(ipages.getTotal());
    res.setPageCount(ipages.getPages());
    return res;
  }


}
