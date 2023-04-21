package com.shmet.helper;


public class GroupNoGenFactory {
    private static final int groupIdBits = 10000;


    public static long genId(long subProjectId, int groupId) {
        return (subProjectId * groupIdBits) + groupId;
    }

    public static void main(String[] args) {
        GroupNoGenFactory GroupNoGenFactory = new GroupNoGenFactory();
        System.out.println(GroupNoGenFactory.genId(20180001001L, 99));
    }
}
