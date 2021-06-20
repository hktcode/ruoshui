package com.hktcode.queue;

public class FetchTqueueGauges
{
    public static FetchTqueueGauges of()
    {
        return new FetchTqueueGauges();
    }

    public long trycnt = 0;

    public long rowcnt = 0;

    private FetchTqueueGauges()
    {
    }
}
