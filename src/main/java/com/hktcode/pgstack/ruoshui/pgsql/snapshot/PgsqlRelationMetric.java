/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PgReplRelation;

public class PgsqlRelationMetric
{
    public static PgsqlRelationMetric of(PgReplRelation relationInfo)
    {
        if (relationInfo == null) {
            throw new ArgumentNullException("relationInfo");
        }
        return new PgsqlRelationMetric(relationInfo);
    }

    public final PgReplRelation relationInfo;

    @Deprecated
    public long lockDuration = -1;

    public long selectMillis = -1;

    public long tuplevalSize = -1;

    private PgsqlRelationMetric(PgReplRelation relationInfo)
    {
        this.relationInfo = relationInfo;
    }
}
