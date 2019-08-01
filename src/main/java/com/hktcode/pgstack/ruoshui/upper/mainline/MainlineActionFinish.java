/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.method.SimpleMethodDelResult;
import com.hktcode.bgsimple.method.SimpleMethodGetResult;
import com.hktcode.bgsimple.method.SimpleMethodPstResult;
import com.hktcode.bgsimple.method.SimpleMethodPutResult;
import com.hktcode.bgsimple.status.SimpleStatusInner;

public class MainlineActionFinish //
    implements MainlineAction<MainlineActionFinish>
{
    public static MainlineActionFinish of()
    {
        return new MainlineActionFinish();
    }

    private MainlineActionFinish()
    {
    }

    @Override
    public SimpleMethodPstResult<MainlineActionFinish> pst()
    {
        return null;
    }

    @Override
    public SimpleMethodPutResult<MainlineActionFinish> put()
    {
        return null;
    }

    @Override
    public SimpleMethodGetResult<MainlineActionFinish> get()
    {
        return null;
    }

    @Override
    public SimpleMethodDelResult<MainlineActionFinish> del()
    {
        return null;
    }

    @Override
    public SimpleStatusInner newStatus(MainlineActionFinish wkstep) throws InterruptedException
    {
        return null;
    }
}
