package com.shmet.handler;

import com.shmet.bean.AcconeStatusBean;
import com.shmet.bean.UpdateAcconeBean;
import com.shmet.dao.IAcconeDao;
import com.shmet.dao.mongodb.ProjectConfigDao;
import com.shmet.dao.mysql.TAcconeDao;
import com.shmet.entity.mysql.gen.TAccone;
import com.shmet.helper.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AcconeHandler {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Value("${filepath.acconefirmware.baseurl}")
  private String url;

  private static final String rbl = ".rbl";

  @Autowired
  private TAcconeDao accontDao;

  @Resource
  private IAcconeDao iAcconeDao;




  @Transactional(readOnly = true)
  public UpdateAcconeBean updateFirmware(UpdateAcconeBean updateAcconeBean) {
    updateAcconeBean.setUrl(url + updateAcconeBean.getAcconeId() + "/update-v"
        + updateAcconeBean.getVer() + rbl);
    return updateAcconeBean;
  }

  @Transactional(readOnly = true)
  public List<AcconeStatusBean> getAllAcconeStatus() {
    List<AcconeStatusBean> rst = new ArrayList<>();
    Map<String, TAccone> acconeMap = accontDao.findAllUsingAcconeMappedByAcconeId();
    for (String acconeId : acconeMap.keySet()) {
      TAccone accone = acconeMap.get(acconeId);
      AcconeStatusBean bean = accontDao.getAcconeStatus(acconeId);
      if (bean == null) {
        bean = new AcconeStatusBean();
      }
      bean.settAccone(accone);
      rst.add(bean);
    }
    return rst;
  }

  public List<TAccone> queryTAccone() {
    return iAcconeDao.all();
  }
}
