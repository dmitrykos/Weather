package com.develogical;

public class ClockImpl implements Clock {
    @Override
    public long getTimeMs() {
        return System.currentTimeMillis() / 1000;
    }
}
