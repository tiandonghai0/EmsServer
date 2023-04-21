package com.shmet.dao.mongodb;

import java.util.Collection;

import com.shmet.dao.mongodb.util.MongoSortUtil;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ObjectOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

@Repository
public class FaultRealDataDao extends BatchUpdateDao {

  @Autowired
  private MongoTemplate mongoTemplate;

  /**
   * 聚合查询
   *
   * @param notInTagCodeList notInTagCodeList
   * @param subProjectId     subProjectId
   * @param startTime        开始时间
   * @param endTime          结束时间
   * @param skip             跳过前面skip条
   * @param limit            最大返回多少条
   * @param sortFlag         排序
   * @param sortMethods      2
   * @return AggregationResults<Document>
   */
  public AggregationResults<Document> aggregateFaultRealDataByDeviceTimeTagForPage(
      Collection<String> notInTagCodeList,
      long subProjectId, long startTime, long endTime,
      long skip, long limit, int sortFlag, int sortMethods) {
    Sort sort = MongoSortUtil.sortOrder(sortFlag, sortMethods);
    /**
     * 聚合查询
     * 1. 先 根据 subProjectId 和 hour 进行查询
     * 2.不显示 _id 的 信息
     * 3.metrics 是一个数组 unwind 表示展开 metrics 的每一项
     * 4.project 表示 修改 显示 字段名
     */
    Aggregation agg =
        Aggregation.newAggregation(
            Aggregation.match(Criteria.where("subProjectId").is(subProjectId).and("hour").lte(endTime)
                .gte(startTime)),
            Aggregation.project().andExclude("_id"),
            Aggregation.unwind("metrics"),
            Aggregation.project("deviceNo").and("metrics.timestamp").as("timestamp")
                .and("metrics.datas").as("datas"),
            Aggregation.project("deviceNo").and("timestamp").as("timestamp")
                .and((ObjectOperators.valueOf("datas").toArray())).as("datas"),
            Aggregation.unwind("datas"),
            Aggregation.match(Criteria.where("datas.k").nin(notInTagCodeList)),
            Aggregation.sort(sort),
            Aggregation.facet(Aggregation.skip(skip), Aggregation.limit(limit)).as("result")
                .and(Aggregation.count().as("count")).as("totalCount")
        );
    return mongoTemplate.aggregate(agg, "faultRealData", Document.class);
  }

}
