/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PgReplAttribute;
import com.hktcode.pgjdbc.PgReplRelation;
import com.hktcode.pgjdbc.PgReplRelationMetadata;

import java.util.ArrayList;
import java.util.List;

public class PgStructRelation
{
    public static PgStructRelation of(long relident, String dbschema, String relation, long replchar)
    {
        if (dbschema == null) {
            throw new ArgumentNullException("dbschema");
        }
        if (relation == null) {
            throw new ArgumentNullException("relation");
        }
        return new PgStructRelation(relident, dbschema, relation, replchar);
    }

    public final PgReplRelationMetadata metadata;

    public final List<PgReplAttribute> attrlist;

    private PgStructRelation(long relident, String dbschema, String relation, long replchar)
    {
        this.metadata = PgReplRelationMetadata.of(relident, dbschema, relation, replchar);
        this.attrlist = new ArrayList<>();
    }

    public PgStructRelainfo builder()
    {
        PgReplRelation relation = PgReplRelation.of(this.metadata, ImmutableList.copyOf(this.attrlist));
        return PgStructRelainfo.of(relation);
    }
}