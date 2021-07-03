package com.hktcode.queue;

import com.fasterxml.jackson.databind.JsonNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class Xspins
{
    public static final long WAIT_TIMEOUT = 128;

    public static final long SPINS_MAXCNT = 1024;

    public static final long LOG_DURATION = 5 * 60 * 1000;

    public static Xspins of()
    {
        return new Xspins();
    }

    public static final int RESET = 0;
    public static final int SPINS = 1;
    public static final int YIELD = 2;
    public static final int SLEEP = 3;

    // config
    public long waitTimeout = WAIT_TIMEOUT;
    public long spinsMaxcnt = SPINS_MAXCNT;
    public long logDuration = LOG_DURATION;

    // gauges
    public long spinsCounts = 0;
    public long yieldCounts = 0;
    public long sleepCounts = 0;
    public long yieldMillis = 0;
    public long sleepMillis = 0;
    public long spinsStarts = 0;

    private Xspins()
    {
    }

    public void pst(JsonNode json)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        waitTimeout = json.path("wait_timeout").asLong(waitTimeout);
        spinsMaxcnt = json.path("spins_maxcnt").asLong(spinsMaxcnt);
        logDuration = json.path("log_duration").asLong(logDuration);
    }

    public int spins(long spins) throws InterruptedException
    {
        if (spins > spinsMaxcnt) {
            long starts = System.currentTimeMillis();
            ++sleepCounts;
            long duration = starts - spinsStarts;
            long millis = this.waitTimeout - duration;
            millis = millis > 0 ? millis : 0;
            Thread.sleep(millis);
            long finish = System.currentTimeMillis();
            sleepMillis += finish - starts;
            return RESET;
        } else if ((spinsCounts + yieldCounts) % 2 == 0) {
            ++spinsCounts;
            spinsStarts = spins == 0 ? System.currentTimeMillis() : spinsStarts;
            return spins == spinsMaxcnt ? SLEEP : YIELD;
        } else {
            long starts = System.currentTimeMillis();
            ++yieldCounts;
            Thread.yield();
            long finish = System.currentTimeMillis();
            yieldMillis += finish + starts;
            return spins == spinsMaxcnt ? SLEEP : SPINS;
        }
    }
}
