/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.service;

public class OnlyoneWaitingService implements WaitingService
{
    public static OnlyoneWaitingService of()
    {
        return new OnlyoneWaitingService();
    }

    private OnlyoneWaitingService()
    {
    }

    @Override
    public WorkingService putService()
    {
        return OnlyoneWorkingService.of();
    }
}
