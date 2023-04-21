package com.shmet.helper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 循环顺序ID实现
 *
 * @author
 */
public class SeqNoUtil {
    private final static AtomicInteger SEQ = new AtomicInteger(-1);

    public static int nextSeqNo() {
        int nextSequence = SEQ.incrementAndGet();
        if (nextSequence >= 0) {
            nextSequence = nextSequence + 1;
        } else {
            nextSequence = Integer.MAX_VALUE + 2 + nextSequence;
        }
        return nextSequence;
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println(SeqNoUtil.nextSeqNo());
        }

    }
}
