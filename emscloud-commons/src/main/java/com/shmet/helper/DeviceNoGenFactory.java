package com.shmet.helper;

public class DeviceNoGenFactory {
    private static final int deviceIdBits = 10000;


    public static long genId(long subProjectId, int deviceId) {
        return (subProjectId * deviceIdBits) + deviceId;
    }

    public static void main(String[] args) {
        DeviceNoGenFactory deviceNoGenFactory = new DeviceNoGenFactory();
        System.out.println(deviceNoGenFactory.genId(20180001001L, 99));
    }
}
