/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PgReplAttribute;
import com.hktcode.pgjdbc.PgReplRelation;
import com.hktcode.pgjdbc.PgReplRelationMetadata;
import com.hktcode.pgstack.ruoshui.pgsql.PgsqlAttribute;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;

import java.util.ArrayList;
import java.util.List;

public class RelationBuilder
{
    public static RelationBuilder of(long relident, String dbschema, String relation, long replchar)
    {
        if (dbschema == null) {
            throw new ArgumentNullException("dbschema");
        }
        if (relation == null) {
            throw new ArgumentNullException("relation");
        }
        return new RelationBuilder(relident, dbschema, relation, replchar);
    }

    public final PgReplRelationMetadata metadata;

    public final List<PgReplAttribute> attrlist;

    private RelationBuilder(long relident, String dbschema, String relation, long replchar)
    {
        this.metadata = PgReplRelationMetadata.of(relident, dbschema, relation, replchar);
        this.attrlist = new ArrayList<>();
    }

    public PgsqlRelationMetric builder()
    {
        PgReplRelation relation = PgReplRelation.of(this.metadata, ImmutableList.copyOf(this.attrlist));
        return PgsqlRelationMetric.of(relation);
    }
}
