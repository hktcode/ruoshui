/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalCreateTupleMsg;
import com.hktcode.pgjdbc.PgReplAttribute;
import com.hktcode.pgjdbc.PgReplComponent;
import com.hktcode.pgjdbc.PgReplRelation;
import org.postgresql.jdbc.PgConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

class PgsenderActionDataTupleval extends PgsenderActionData
{
    static PgsenderActionDataTupleval of(PgsenderActionDataSrBegins action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderActionDataTupleval(action);
    }

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportRelaLock relaLock;

    public final PgsenderReportReplSlotTuple replSlot;

    public final PgsenderReportSizeDiff sizeDiff;

    public final PgsenderReportSsBegins ssbegins;

    public final ImmutableList<PgsqlRelationMetric> relationLst;

    final Iterator<PgsqlRelationMetric> relIterator;

    final PgsqlRelationMetric curRelation;

    private PgsenderActionDataTupleval(PgsenderActionDataSrBegins action) //
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
    public PgsenderAction next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl) //
        throws SQLException, InterruptedException
    {
        if (exesvc == null) {
            throw new ArgumentNullException("exesvc");
        }
        if (pgdata == null) {
            throw new ArgumentNullException("pgdata");
        }
        if (pgrepl == null) {
            throw new ArgumentNullException("pgrepl");
        }
        PgReplRelation r = this.curRelation.relationInfo;
        final long lsn = this.replSlot.createTuple.consistentPoint;
        try (PreparedStatement ps = this.config.queryTupleval(pgdata, r)) {
            PgRecord record = null;
            ResultSet rs = null;
            Boolean next = null;
            Future<ResultSet> rsFuture = exesvc.submit(DeputeExecuteQueryMainline.of(ps));
            Future<Boolean> nextFuture = null;
            DeputeResultSetNextMainline rsDepute = null;
            while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
                if (rs == null) {
                    rs = this.pollFromFuture(rsFuture);
                } else if (rsDepute == null) {
                    rsDepute = DeputeResultSetNextMainline.of(rs);
                } else if (record != null) {
                    record = this.send(record);
                } else if (nextFuture == null) {
                    nextFuture = exesvc.submit(rsDepute);
                } else if (next == null) {
                    next = this.pollFromFuture(nextFuture);
                } else if (next) {
                    LogicalCreateTupleMsg msg = this.build(rs);
                    record = this.config.createMessage(lsn, msg);
                    nextFuture = null;
                    next = null;
                } else {
                    return PgsenderActionDataSrFinish.of(this);
                }
            }
        }
        return PgsenderActionTerminateEnd.of(this);
    }

    private LogicalCreateTupleMsg build(ResultSet rs) throws SQLException
    {
        PgReplRelation relation = this.curRelation.relationInfo;
        final JsonNode oldvalue = MissingNode.getInstance();
        ++this.curRelation.tuplevalSize;
        List<PgReplComponent> tuple = new ArrayList<>();
        for (PgReplAttribute attr : relation.attrlist) {
            String v = rs.getString(attr.attrname);
            JsonNode newvalue = (v == null ? NullNode.getInstance() : TextNode.valueOf(v));
            tuple.add(PgReplComponent.of(attr, oldvalue, newvalue));
        }
        return LogicalCreateTupleMsg.of(relation, ImmutableList.copyOf(tuple));
    }

    @Override
    public PgsenderMetricRunTupleval toRunMetrics()
    {
        return PgsenderMetricRunTupleval.of(this);
    }

    @Override
    public PgsenderMetricEndTupleval toEndMetrics()
    {
        return PgsenderMetricEndTupleval.of(this);
    }
}
