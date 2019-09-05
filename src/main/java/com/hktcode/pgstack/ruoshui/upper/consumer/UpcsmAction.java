/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.bgsimple.status.SimpleStatusInner;
import com.hktcode.pgstack.ruoshui.upper.junction.UpjctResult;

public interface UpcsmAction extends BgWorker<UpcsmAction>
{
    UpcsmActionErr next(Throwable throwsError) throws InterruptedException;

    SimpleStatusInner newStatus(UpcsmAction wkstep) throws InterruptedException;

    UpcsmResult get() throws InterruptedException;

    default UpcsmResult pst() throws InterruptedException
    {
        return this.get();
    }

    default UpcsmResult put() throws InterruptedException
    {
        return this.get();
    }

    default UpcsmResult del() throws InterruptedException
    {
        return this.get();
    }
}
