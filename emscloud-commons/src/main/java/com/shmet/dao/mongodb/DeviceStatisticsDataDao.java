package com.shmet.dao.mongodb;

import java.util.Collection;
import java.util.List;

import com.shmet.dao.mongodb.util.MongoSortUtil;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ObjectOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import com.shmet.entity.mongo.DeviceDayStatistics;
import com.shmet.entity.mongo.DeviceMonthStatistics;

@Repository
public class DeviceStatisticsDataDao extends BatchUpdateDao {
  @Autowired
  private MongoTemplate mongoTemplate;

  public List<DeviceMonthStatistics> findAllByMonth(long time) {
    Query query = Query.query(Criteria.where("yearMonth").is(time));
    return mongoTemplate.find(query, DeviceMonthStatistics.class);
  }

  public List<DeviceDayStatistics> findAllByDay(long time) {
    Query query = Query.query(Criteria.where("day").is(time));
    return mongoTemplate.find(query, DeviceDayStatistics.class);
  }

  public List<DeviceDayStatistics> findAllDeviceDayStatisticsByTime(List<Long> deviceNoList, String startTime, String endTime) {
    long from = Long.parseLong(startTime);
    long to = Long.parseLong(endTime);
    Query query = Query.query(Criteria.where("day").lte(to).gte(from).and("deviceNo").in(deviceNoList));
    return mongoTemplate.find(query, DeviceDayStatistics.class);
  }

  @Deprecated
  public List<DeviceDayStatistics> findAllDeviceDayStatisticsByTime(String startTime, String endTime) {
    long from = Long.parseLong(startTime);
    long to = Long.parseLong(endTime);
    Query query = Query.query(Criteria.where("day").lte(to).gte(from));
    return mongoTemplate.find(query, DeviceDayStatistics.class);
  }

  public List<DeviceMonthStatistics> findByDeviceNoTime(String deviceNo, String from, String to) {
    long deviceNoLong = Long.parseLong(deviceNo);
    long startTime = Long.parseLong(from);
    long endTime = Long.parseLong(to);
    Query query =
        Query.query(Criteria.where("deviceNo").is(deviceNoLong).and("yearMonth").lte(endTime)
            .gte(startTime));
    query.with(Sort.by(new Order(Direction.ASC, "yearMonth")));
    return mongoTemplate.find(query, DeviceMonthStatistics.class);
  }

  public AggregationResults<Document> aggregateMonthByDeviceTimeTagForPage(
      Collection<Long> deviceNoList, Collection<String> tagCodeList, long startTime, long endTime,
      long skip, long limit, int sortFlag, int sortMethods) {
    Sort.Order orderTime = MongoSortUtil.sortOrderTime(sortMethods, null);
    Sort.Order orderDeviceNo = MongoSortUtil.sortOrderDeviceNo(sortMethods, null);

    Sort sort;
    if (sortFlag == 2) {
      sort = Sort.by(orderTime, orderDeviceNo);
    } else {
      sort = Sort.by(orderDeviceNo, orderTime);
    }
    Aggregation agg =
        Aggregation.newAggregation(
            Aggregation.match(Criteria.where("deviceNo").in(deviceNoList).and("yearMonth")
                .lte(endTime).gte(startTime)),
            Aggregation.project("deviceNo").and("yearMonth").as("time")
                .and((ObjectOperators.valueOf("statistics").toArray())).as("statistics")
                .andExclude("_id"),
            Aggregation.unwind("statistics"),
            Aggregation.match(Criteria.where("statistics.k").in(tagCodeList)),
            Aggregation.sort(sort),
            Aggregation.facet(Aggregation.skip(skip), Aggregation.limit(limit)).as("result")
                .and(Aggregation.count().as("count")).as("totalCount")
        );
    return mongoTemplate.aggregate(agg, "deviceMonthStatistics", Document.class);
  }

  public AggregationResults<Document> aggregateDayByDeviceTimeTagForPage(
      Collection<Long> deviceNoList, Collection<String> tagCodeList, long startTime, long endTime,
      long skip, long limit, int sortFlag, int sortMethods) {
    Sort.Order orderTime = MongoSortUtil.sortOrderTime(sortMethods, null);
    Sort.Order orderDeviceNo = MongoSortUtil.sortOrderDeviceNo(sortMethods, null);

    Sort sort;
    if (sortFlag == 2) {
      sort = Sort.by(orderTime, orderDeviceNo);
    } else {
      sort = Sort.by(orderDeviceNo, orderTime);
    }
    Aggregation agg =
        Aggregation.newAggregation(
            Aggregation.match(Criteria.where("deviceNo").in(deviceNoList).and("day").lte(endTime)
                .gte(startTime)),
            Aggregation.project("deviceNo").and("day").as("time")
                .and((ObjectOperators.valueOf("statistics").toArray())).as("statistics")
                .andExclude("_id"),
            Aggregation.unwind("statistics"),
            Aggregation.match(Criteria.where("statistics.k").in(tagCodeList).and("statistics.v.price").exists(true)),
            Aggregation.sort(sort),
            Aggregation.facet(Aggregation.skip(skip), Aggregation.limit(limit)).as("result")
                .and(Aggregation.count().as("count")).as("totalCount")
        );
    return mongoTemplate.aggregate(agg, "deviceDayStatistics", Document.class);
  }
}
