/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.pgjdbc.PgReplAttribute;
import org.postgresql.jdbc.PgConnection;

import javax.script.ScriptException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

abstract class PgActionDataQueryRel extends PgActionDataQuerySql
{
    PgActionDataQueryRel
        /* */( PgConfig config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<PgRecord> tqueue //
        /* */)
    {
        super(config, status, tqueue);
        this.logDatetime = super.actionStart;
    }

    PgActionDataQueryRel(PgActionData action, long actionStart)
    {
        super(action, actionStart);
        this.logDatetime = action.logDatetime;
    }

    final List<PgStructRelainfo> newRelalist = new ArrayList<>();

    PgStructRelation[] relaBuilder = new PgStructRelation[0];

    @Override
    PgRecord build(ResultSet rs) //
        throws SQLException, ScriptException
    {
        long relident = rs.getLong("relident");
        long attflags = rs.getLong("attflags");
        String attrname = rs.getString("attrname");
        long datatype = rs.getLong("datatype");
        long attypmod = rs.getLong("attypmod");
        String tpschema = rs.getString("tpschema");
        String typename = rs.getString("typename");
        if (this.relaBuilder.length == 0) {
            String dbschema = rs.getString("dbschema");
            String relation = rs.getString("relation");
            long replchar = rs.getLong("replchar");
            this.relaBuilder = new PgStructRelation[] {
                PgStructRelation.of(relident, dbschema, relation, replchar)
            };
        } else if (this.relaBuilder[0].metadata.relident != relident) {
            PgStructRelainfo r = this.relaBuilder[0].builder();
            if (this.config.whereScript.eval(r.relationInfo)) {
                this.newRelalist.add(r);
            }
            String dbschema = rs.getString("dbschema");
            String relation = rs.getString("relation");
            long replchar = rs.getLong("replchar");
            this.relaBuilder[0] = PgStructRelation.of(relident, dbschema, relation, replchar);
        }
        PgReplAttribute attr = PgReplAttribute.of //
            /* */( attrname //
            /* */, tpschema //
            /* */, typename //
            /* */, -1 //
            /* */, attflags //
            /* */, datatype //
            /* */, attypmod //
            /* */);
        this.relaBuilder[0].attrlist.add(attr);
        return null;
    }

    PreparedStatement queryRelalist(PgConnection pgdata) //
        throws SQLException
    {
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        String sql = this.config.relationSql;
        for (String name : this.config.logicalRepl.publicationNames) {
            arrayNode.add(name);
        }
        PreparedStatement ps = this.preparedStatement(pgdata, sql);
        try {
            ps.setString(1, arrayNode.toString());
            return ps;
        }
        catch (Exception ex) {
            ps.close();
            throw ex;
        }
    }
}
