package com.shmet.handler.v2;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shmet.dao.OperateLogMapper;
import com.shmet.entity.dto.ResultSearch;
import com.shmet.entity.mongo.OperationLog;
import com.shmet.entity.mysql.gen.OperateLog;
import com.shmet.utils.DateUtils;
import com.shmet.vo.req.OpLogConditionQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author
 */
@Service
public class OperationLogService {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Resource
  private OperateLogMapper operateLogMapper;

  public ResultSearch PageQuery(OpLogConditionQuery query) {
    if (query == null) {
      return ResultSearch.getSuccessResultInfo(mongoTemplate.find(
          new Query().limit(10).skip(0),
          OperationLog.class
      ));
    }
    ResultSearch res = new ResultSearch(query.getPageNum(), query.getPageSize());
    Criteria criteria = new Criteria();
    if (query.getLogType() != null) {
      criteria.and("logType").is(query.getLogType());
    }
    if (StringUtils.isNotBlank(query.getAccount())) {
      criteria.and("account").is(query.getAccount());
    }
    if (StringUtils.isNotBlank(query.getOpPlatform())) {
      criteria.and("operatePlatform").regex(query.getOpPlatform());
    }
    if (StringUtils.isNotBlank(query.getOpTime())) {
      criteria.and("operateTime")
          .lte(DateUtils.str2Long(query.getOpTime()).getRight())
          .gte(DateUtils.str2Long(query.getOpTime()).getLeft());
    }

    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(criteria),
        Aggregation.project("account", "operatePlatform", "username", "logType", "content", "clientIp", "operateTime")
            .andExclude("_id"),
        Aggregation.limit(query.getPageSize()),
        new SkipOperation((long) (query.getPageNum() - 1) * query.getPageSize()),
        Aggregation.sort(Sort.by("operateTime").descending())
    );
    long totalCount = mongoTemplate.count(Query.query(criteria), "operationLog");
    long totalPage = (totalCount / res.pageSize) == 0 ? (totalCount / res.pageSize) : (totalCount / res.pageSize) + 1;

    AggregationResults<OperationLog> operationLogs = mongoTemplate.aggregate(agg, "operationLog", OperationLog.class);
    List<OperationLog> opLogs = operationLogs.getMappedResults();
    res.setData(opLogs);
    res.setSuccess(true);
    res.setCode("1");
    res.setMessage("查询成功");
    res.setPageCount(totalPage);
    res.setTotalCount(totalCount);
    return res;
  }

  public ResultSearch pageQueryMySQL(OpLogConditionQuery query) {
    ResultSearch res = new ResultSearch(query.getPageNum(), query.getPageSize());
    Page<OperateLog> page = new Page<>(query.getPageNum(), query.getPageSize());

    LambdaQueryWrapper<OperateLog> wrapper = Wrappers.lambdaQuery();
    if (query.getLogType() != null) {
      wrapper.eq(OperateLog::getLogType, query.getLogType());
    }
    if (StringUtils.isNotBlank(query.getOpTime())) {
      wrapper.likeRight(OperateLog::getTime, query.getOpTime());
    }
    if (StringUtils.isNotBlank(query.getAccount())) {
      wrapper.like(OperateLog::getUsername, query.getAccount());
    }
    if (StringUtils.isNotBlank(query.getOpPlatform())) {
      wrapper.like(OperateLog::getOpPlatform, query.getOpPlatform());
    }
    wrapper.orderByDesc(OperateLog::getTime);

    IPage<OperateLog> pageRes = this.operateLogMapper.selectPage(page, wrapper);
    res.setPageCount(pageRes.getPages());
    res.setTotalCount(pageRes.getTotal());
    res.setSuccess(true);
    res.setData(pageRes.getRecords());
    return res;
  }
}
