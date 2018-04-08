package com.rbc.ex.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Unique Id generator class.
 */
public class IdGenerator {

    private AtomicLong id = new AtomicLong(0);

    public long getNextId() {
        return id.incrementAndGet();
    }

}
