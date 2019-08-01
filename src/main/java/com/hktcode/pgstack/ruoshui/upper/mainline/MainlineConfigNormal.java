/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PgReplRelation;
import com.hktcode.pgstack.ruoshui.pgsql.LogicalReplConfig;
import com.hktcode.pgstack.ruoshui.pgsql.PgsqlRelation;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotFilter;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;
import org.postgresql.jdbc.PgConnection;

import javax.script.ScriptException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainlineConfigNormal
{
    public PreparedStatement queryTypelist(PgConnection pgdata)
        throws SQLException
    {
        PreparedStatement ps = pgdata.prepareStatement
            /* */( TYPES_SELECT //
                /* */, ResultSet.TYPE_FORWARD_ONLY //
                /* */, ResultSet.CONCUR_READ_ONLY //
                /* */, ResultSet.CLOSE_CURSORS_AT_COMMIT //
                /* */);
        try {
            ps.setFetchDirection(ResultSet.FETCH_FORWARD);
            ps.setFetchSize(this.rsFetchsize);
            return ps;
        }
        catch (Exception ex) {
            ps.close();
            throw ex;
        }

    }

    private static final String TYPES_SELECT = "" //
        + "\n select \"t\".    \"oid\"::int8 as \"datatype\" " //
        + "\n      , \"n\".\"nspname\"::text as \"tpschema\" " //
        + "\n      , \"t\".\"typname\"::text as \"typename\" " //
        + "\n  from            \"pg_catalog\".\"pg_type\"      \"t\" " //
        + "\n       inner join \"pg_catalog\".\"pg_namespace\" \"n\" " //
        + "\n               on \"t\".\"typnamespace\" = \"n\".\"oid\" " //
        + "\n ";

    public PreparedStatement queryTupleval(PgConnection pgdata, PgReplRelation relation)
            throws SQLException
    {
        String sql = buildSelect(pgdata, relation);
        PreparedStatement ps = pgdata.prepareStatement
            /* */( sql //
            /* */, ResultSet.TYPE_FORWARD_ONLY //
            /* */, ResultSet.CONCUR_READ_ONLY //
            /* */, ResultSet.CLOSE_CURSORS_AT_COMMIT //
            /* */);
        try {
            ps.setFetchDirection(ResultSet.FETCH_FORWARD);
            ps.setFetchSize(this.rsFetchsize);
            return ps;
        }
        catch (Exception ex) {
            ps.close();
            throw ex;
        }

    }

    static String buildSelect(PgConnection c, PgReplRelation r) //
        throws SQLException
    {
        StringBuilder sb = new StringBuilder("\nSELECT ");
        String n = c.escapeIdentifier(r.attrlist.get(0).attrname);
        sb.append(n);
        sb.append("::text as ");
        sb.append(n);
        for(int i = 1; i < r.attrlist.size(); ++i) {
            sb.append("\n     , ");
            n = c.escapeIdentifier(r.attrlist.get(i).attrname);
            sb.append(n);
            sb.append("::text as ");
            sb.append(n);
        }
        sb.append("\nFROM ");
        sb.append(c.escapeIdentifier(r.dbschema));
        sb.append(".");
        sb.append(c.escapeIdentifier(r.relation));
        return sb.toString();
    }

    public PreparedStatement queryRelalist(Connection connection)
        throws SQLException
    {
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (String name : this.pubnameList) {
            arrayNode.add(name);
        }
        PreparedStatement ps = connection.prepareStatement
            /* */( relationSql //
            /* */, ResultSet.TYPE_FORWARD_ONLY //
            /* */, ResultSet.CONCUR_READ_ONLY //
            /* */, ResultSet.CLOSE_CURSORS_AT_COMMIT //
            /* */);
        try {
            ps.setFetchDirection(ResultSet.FETCH_FORWARD);
            ps.setFetchSize(this.rsFetchsize);
            ps.setString(1, arrayNode.toString());
            return ps;
        }
        catch (Exception ex) {
            ps.close();
            throw ex;
        }
    }

    public boolean whereRelalist(PgReplRelation relation) throws ScriptException
    {
        if (relation == null) {
            throw new ArgumentNullException("relation");
        }
        return whereScript.eval(relation);
    }

    public final String relationSql;

    public final ImmutableList<String> pubnameList;

    public final PgSnapshotFilter whereScript;

    public final int rsFetchsize = 1024;

    public final PgLockMode lockingMode;

    public final String slotnameInf;

    public final LogicalReplConfig logicalRepl;

    public long waitTimeout;

    public long logDuration;

    MainlineConfigNormal //
        /* */( String relationSql //
        /* */, long waitTimeout //
        /* */, long logDuration //
        /* */, ImmutableList<String> pubnameList //
        /* */, PgSnapshotFilter whereScript //
        /* */, PgLockMode lockingMode //
        /* */, String slotnameInf //
        /* */, LogicalReplConfig logicalRepl
        /* */) //
    {
        this.waitTimeout = waitTimeout;
        this.logDuration = logDuration;
        this.relationSql = relationSql;
        this.pubnameList = pubnameList;
        this.whereScript = whereScript;
        this.lockingMode = lockingMode;
        this.slotnameInf = slotnameInf;
        this.logicalRepl = logicalRepl;
    }

    public String lockRelation(PgReplRelation relation, PgConnection cnt)
        throws SQLException
    {
        if (relation == null) {
            throw new ArgumentNullException("relation");
        }
        return "LOCK TABLE ONLY " //
            + cnt.escapeIdentifier(relation.dbschema) //
            + "." //
            + cnt.escapeIdentifier(relation.relation) //
            + " IN " //
            + lockingMode.textFormat //
            + " MODE" //
            ;
    }

}
