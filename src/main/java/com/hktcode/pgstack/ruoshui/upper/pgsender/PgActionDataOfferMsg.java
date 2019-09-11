/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalBegRelationMsg;
import com.hktcode.pgjdbc.PgReplRelation;
import org.postgresql.jdbc.PgConnection;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;

abstract class PgActionDataOfferMsg extends PgActionData
{
    PgActionDataOfferMsg(PgActionData action, long actionStart)
    {
        super(action, actionStart);
    }

    PgActionDataOfferMsg(PgActionData action)
    {
        super(action, action.actionStart);
    }

    @Override
    public PgAction next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl) //
        throws InterruptedException
    {
        PgRecord record = this.createRecord();
        while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
            if ((record = this.send(record)) == null) {
                return this.complete();
            }
        }
        return PgActionTerminateEnd.of(this);
    }

    abstract PgRecord createRecord();

    abstract PgAction complete();
}
