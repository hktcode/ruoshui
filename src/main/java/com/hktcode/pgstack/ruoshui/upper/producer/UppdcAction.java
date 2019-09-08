/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.producer;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.bgsimple.status.SimpleStatusInner;

public interface UppdcAction extends BgWorker<UppdcAction>
{
    UppdcActionErr next(Throwable throwsError);

    SimpleStatusInner newStatus(UppdcAction action) throws InterruptedException;

    UppdcResult get();

    default UppdcResult pst()
    {
        return this.get();
    }

    default UppdcResult put()
    {
        return this.get();
    }

    default UppdcResult del()
    {
        return this.get();
    }
}
