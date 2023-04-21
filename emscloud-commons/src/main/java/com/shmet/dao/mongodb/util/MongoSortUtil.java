package com.shmet.dao.mongodb.util;

import org.springframework.data.domain.Sort;

public final class MongoSortUtil {
  public static Sort sortOrder(int sortFlag, int sortMethods) {
    Sort.Order orderTime = null;
    Sort.Order orderDeviceNo = null;
    if ((sortMethods & 2) == 2) {
      orderTime = new Sort.Order(Sort.Direction.DESC, "timestamp");
    } else if ((sortMethods & 2) == 0) {
      orderTime = new Sort.Order(Sort.Direction.ASC, "timestamp");
    }

    if ((sortMethods & 1) == 1) {
      orderDeviceNo = new Sort.Order(Sort.Direction.DESC, "deviceNo");
    } else if ((sortMethods & 1) == 0) {
      orderDeviceNo = new Sort.Order(Sort.Direction.ASC, "deviceNo");
    }

    Sort sort;
    if (sortFlag == 2) {
      sort = Sort.by(orderTime, orderDeviceNo);
    } else {
      sort = Sort.by(orderDeviceNo, orderTime);
    }
    return sort;
  }

  public static Sort.Order sortOrderTime(int sortMethods, Sort.Order orderTime) {
    if ((sortMethods & 2) == 2) {
      orderTime = new Sort.Order(Sort.Direction.DESC, "time");
    } else if ((sortMethods & 2) == 0) {
      orderTime = new Sort.Order(Sort.Direction.ASC, "time");
    }
    return orderTime;
  }

  public static Sort.Order sortOrderDeviceNo(int sortMethods, Sort.Order orderDeviceNo) {
    if ((sortMethods & 1) == 1) {
      orderDeviceNo = new Sort.Order(Sort.Direction.DESC, "deviceNo");
    } else if ((sortMethods & 1) == 0) {
      orderDeviceNo = new Sort.Order(Sort.Direction.ASC, "deviceNo");
    }
    return orderDeviceNo;
  }
}
