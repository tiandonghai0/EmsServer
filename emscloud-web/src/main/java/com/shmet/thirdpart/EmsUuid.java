package com.shmet.thirdpart;

/**
 * @author
 */
public class EmsUuid {
  private static final EmsUuid instance = new EmsUuid();

  private SnowflakeIdWorker idWorker;

  public static EmsUuid getInstance() {
    return instance;
  }

  private EmsUuid() {
  }

  public void init(long centerId, long workerId) {
    idWorker = new SnowflakeIdWorker(workerId, centerId);
  }

  public String getUUId() {
    return String.valueOf(idWorker.nextId());
  }
}
