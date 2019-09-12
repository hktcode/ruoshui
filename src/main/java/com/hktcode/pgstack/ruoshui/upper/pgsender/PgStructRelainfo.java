/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PgReplRelation;

public class PgStructRelainfo
{
    public static PgStructRelainfo of(PgReplRelation relationInfo)
    {
        if (relationInfo == null) {
            throw new ArgumentNullException("relationInfo");
        }
        return new PgStructRelainfo(relationInfo);
    }

    public final PgReplRelation relationInfo;

    public long lockDuration = -1;

    public long selectMillis = -1;

    public long tuplevalSize = -1;

    private PgStructRelainfo(PgReplRelation relationInfo)
    {
        this.relationInfo = relationInfo;
    }
}
