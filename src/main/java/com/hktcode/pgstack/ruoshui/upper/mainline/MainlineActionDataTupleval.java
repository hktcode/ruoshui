/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

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
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;
import org.postgresql.jdbc.PgConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

class MainlineActionDataTupleval //
    extends MainlineActionData<MainlineActionDataTupleval, MainlineConfigSnapshot> //
{
    static MainlineActionDataTupleval of(MainlineActionDataSrBegins action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineActionDataTupleval(action);
    }

    public final MainlineReportBegin1st begin1st;

    public final MainlineReportRelaList relalist;

    public final MainlineReportRelaLock relaLock;

    public final MainlineReportReplSlot replSlot;

    public final MainlineReportSizeDiff sizeDiff;

    public final MainlineReportSsBegins ssbegins;

    public final ImmutableList<PgsqlRelationMetric> relationLst;

    public final Iterator<PgsqlRelationMetric> relIterator;

    public final PgsqlRelationMetric curRelation;

    private MainlineActionDataTupleval(MainlineActionDataSrBegins action) //
    {
        super(action, action.actionStart);
        this.begin1st = action.begin1st;
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
    MainlineAction next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl) //
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
            MainlineRecord record = null;
            ResultSet rs = null;
            Boolean next = null;
            Future<ResultSet> rsFuture = exesvc.submit(MainlineDeputeExecuteQuery.of(ps));
            Future<Boolean> nextFuture = null;
            MainlineDeputeResultSetNext rsDepute = null;
            while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
                if (rs == null) {
                    rs = this.pollFromFuture(rsFuture);
                } else if (rsDepute == null) {
                    rsDepute = MainlineDeputeResultSetNext.of(rs);
                } else if (record != null) {
                    record = this.send(record);
                } else if (nextFuture == null) {
                    nextFuture = exesvc.submit(rsDepute);
                } else if (next == null) {
                    next = this.pollFromFuture(nextFuture);
                } else if (next) {
                    LogicalCreateTupleMsg msg = this.build(rs);
                    record = MainlineRecordNormal.of(lsn, msg);
                    nextFuture = null;
                    next = null;
                } else {
                    return MainlineActionDataSrFinish.of(this);
                }
            }
        }
        return MainlineActionTerminateEnd.of(this);
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
    public MainlineMetricRunTupleval toRunMetrics()
    {
        return MainlineMetricRunTupleval.of(this);
    }

    @Override
    public MainlineMetricEndTupleval toEndMetrics()
    {
        return MainlineMetricEndTupleval.of(this);
    }
}
