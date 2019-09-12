/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.bgsimple.status.SimpleStatusInner;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.replication.LogSequenceNumber;

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

    default UpcsmResult pst(LogSequenceNumber lsn) throws InterruptedException
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        return this.get();
    }

    default UpcsmResult pst(UpcsmParamsPstSnapshot params) //
        throws InterruptedException
    {
        if (params == null) {
            throw new ArgumentNullException("params");
        }
        return this.get();
    }
}
