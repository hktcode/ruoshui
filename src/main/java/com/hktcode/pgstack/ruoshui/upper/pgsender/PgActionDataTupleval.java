/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.*;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplRelationName;
import org.postgresql.jdbc.PgConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class PgActionDataTupleval extends PgActionDataQuerySql
{
    static PgActionDataTupleval of(PgActionDataSrBegins action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgActionDataTupleval(action);
    }

    final PgReportRelaList relalist;

    final PgReportRelaLock relaLock;

    final PgReportReplSlotTuple replSlot;

    final PgReportSizeDiff sizeDiff;

    final PgReportSsBegins ssbegins;

    final ImmutableList<PgStructRelainfo> relationLst;

    final Iterator<PgStructRelainfo> relIterator;

    final PgStructRelainfo curRelation;

    private PgActionDataTupleval(PgActionDataSrBegins action) //
    {
        super(action, action.actionStart);
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = action.sizeDiff;
        this.ssbegins = action.ssbegins;
        this.relationLst = action.relationLst;
        this.relIterator = action.relIterator;
        this.logDatetime = action.logDatetime;
        this.rsgetCounts = action.rsgetCounts;
        this.rsgetMillis = action.rsgetMillis;
        this.offerMillis = action.offerMillis;
        this.offerCounts = action.offerCounts;
        this.rsnextCount = action.rsnextCount;
        this.recordCount = action.recordCount;
        this.curRelation = action.curRelation;
    }

    @Override
    PgRecord build(ResultSet rs) //
        throws SQLException
    {
        final long lsn = this.replSlot.createTuple.consistentPoint;
        PgReplRelation relation = this.curRelation.relationInfo;
        final JsonNode oldvalue = MissingNode.getInstance();
        ++this.curRelation.tuplevalSize;
        List<PgReplComponent> tuple = new ArrayList<>();
        for (PgReplAttribute attr : relation.attrlist) {
            String v = rs.getString(attr.attrname);
            JsonNode newvalue = (v == null ? NullNode.getInstance() : TextNode.valueOf(v));
            tuple.add(PgReplComponent.of(attr, oldvalue, newvalue));
        }
        LogicalMsg msg = LogicalCreateTupleMsg.of(relation, ImmutableList.copyOf(tuple));
        return PgRecordLogicalMsg.of(lsn, msg);
    }

    @Override
    PgAction complete(Connection pgdata)
    {
        return PgActionDataSrFinish.of(this);
    }

    @Override
    PgDeputeSelectData createDepute(PgConnection pgdata, PgConnection pgrepl) //
        throws SQLException
    {
        PgReplRelation r = this.curRelation.relationInfo;
        PgReplRelationName name = PgReplRelationName.of(r.dbschema, r.relation);
        String sql = this.config.tupleSelect.get(name);
        if (sql == null) {
            sql = buildSelect(pgdata, r);
        }
        PreparedStatement ps = this.preparedStatement(pgdata, sql);
        return PgDeputeSelectData.of(ps);
    }

    private static String buildSelect(PgConnection c, PgReplRelation r) //
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

    @Override
    public PgMetricRunTupleval toRunMetrics()
    {
        return PgMetricRunTupleval.of(this);
    }
}
