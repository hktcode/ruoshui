/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper;

public class UpperServiceWaitingOnlyone implements UpperServiceWaiting
{
    public static UpperServiceWaitingOnlyone of()
    {
        return new UpperServiceWaitingOnlyone();
    }

    private UpperServiceWaitingOnlyone()
    {
    }

    @Override
    public UpperServiceWorking putService()
    {
        return UpperServiceWorkingOnlyone.of();
    }
}
