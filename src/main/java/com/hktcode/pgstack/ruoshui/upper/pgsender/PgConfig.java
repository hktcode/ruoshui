/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.tqueue.TqueueConfig;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.LogicalReplConfig;
import com.hktcode.pgstack.ruoshui.pgsql.PgConnectionProperty;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplRelationName;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

import static com.hktcode.lang.Application.getResourceFileAsString;

public abstract class PgConfig extends TqueueConfig
{
    public static ImmutableMap<PgReplRelationName, String> toTupleSelect(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        Map<PgReplRelationName, String> map = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> it = node.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> e = it.next();
            PgReplRelationName relationName = PgReplRelationName.ofTextString(e.getKey());
            map.put(relationName, e.getValue().asText());
        }
        return ImmutableMap.copyOf(map);
    }

    /**
     * 默认的{@link ResultSet#setFetchSize(int)}值.
     */
    static final int DEFAULT_RS_FETCHSIZE = 10240;

    static final String DEFAULT_RELATION_SQL;

    static final String DEFAULT_TYPELIST_SQL;

    static {
        DEFAULT_TYPELIST_SQL = getResourceFileAsString("default_typelist.sql");
        DEFAULT_RELATION_SQL = getResourceFileAsString("default_relalist.sql");
    }

    public final PgConnectionProperty srcProperty;

    public final ImmutableMap<PgReplRelationName, String> tupleSelect;

    public final String relationSql;

    public final PgFilter whereScript;

    public final PgLockMode lockingMode;

    public final LogicalReplConfig logicalRepl;

    public int rsFetchsize = DEFAULT_RS_FETCHSIZE;

    public final String typelistSql;

    protected PgConfig //
        /* */( PgConnectionProperty srcProperty //
        /* */, String relationSql //
        /* */, PgFilter whereScript //
        /* */, PgLockMode lockingMode //
        /* */, LogicalReplConfig logicalRepl //
        /* */, ImmutableMap<PgReplRelationName, String> tupleSelect //
        /* */, String typelistSql //
        /* */) //
    {
        this.srcProperty = srcProperty;
        this.relationSql = relationSql;
        this.whereScript = whereScript;
        this.lockingMode = lockingMode;
        this.tupleSelect = tupleSelect;
        this.logicalRepl = logicalRepl;
        this.typelistSql = typelistSql;
    }

    public abstract PgAction afterSnapshot(PgActionDataSsFinish action);

    public abstract PgDeputeReplSlot newCreateSlot(Statement statement);

    public abstract PgAction createsAction(AtomicReference<SimpleStatus> status, TransferQueue<PgRecord> tqueue);
}
