package com.shmet.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shmet.dao.ISubProjectDao;
import com.shmet.entity.mysql.gen.TSubProject;

import javax.annotation.Resource;

@Service
public class SubProjectHandler {
  @Resource
  private ISubProjectDao iSubProjectDao;

  public List<TSubProject> queryTSubProject() {
    return iSubProjectDao.all();
  }

}
