/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.junction;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.bgsimple.method.SimpleMethodDelResult;
import com.hktcode.bgsimple.method.SimpleMethodGetResult;
import com.hktcode.bgsimple.method.SimpleMethodPstResult;
import com.hktcode.bgsimple.method.SimpleMethodPutResult;
import com.hktcode.bgsimple.status.SimpleStatusInner;

import javax.script.ScriptException;

interface UpjctAction extends BgWorker<UpjctAction>
{
    UpjctActionErr next(Throwable throwsError);

    SimpleStatusInner newStatus(UpjctAction action) throws InterruptedException;

    UpjctResult get();

    default UpjctResult pst()
    {
        return this.get();
    }

    default UpjctResult put()
    {
        return this.get();
    }

    default UpjctResult del()
    {
        return this.get();
    }
}
