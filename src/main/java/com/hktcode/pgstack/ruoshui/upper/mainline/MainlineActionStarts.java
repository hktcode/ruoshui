package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalDatatypeInfMsg;
import com.hktcode.pgjdbc.PostgreSQL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;

class MainlineActionStarts extends MainlineAction
{
    static MainlineActionStarts
    of(MainlineConfig config, MainlineSender sender, ZonedDateTime starts)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (sender == null) {
            throw new ArgumentNullException("sender");
        }
        if (starts == null) {
            throw new ArgumentNullException("starts");
        }
        MainlineMetricDatatype metric = MainlineMetricDatatype.of(starts);
        return new MainlineActionStarts(config, sender, metric);
    }

    private MainlineActionStarts //
        (MainlineConfig config, MainlineSender sender, MainlineMetricDatatype metric)
    {
        super(sender);
        this.config = config;
        this.metric = metric;
    }

    private final MainlineConfig config;

    private final MainlineMetricDatatype metric;

    MainlineAction next() throws SQLException, InterruptedException
    {
        MainlineRecord r = null;
        metric.statusInfor = "query data types";
        final int fetchSize = 128;
        try (Connection c = config.srcProperty.queriesConnection();
             Statement s = PostgreSQL.createStatement(c, fetchSize);
             ResultSet rs = s.executeQuery(TYPES_SELECT)) {
            rs.setFetchDirection(ResultSet.FETCH_FORWARD);
            rs.setFetchSize(fetchSize);
            while (!sender.isDone()) {
                long timeout = config.logDuration;
                if (r != null) {
                    r = this.sender.push(r, timeout, timeout, metric);
                } else if (rs.next()) {
                    long d = rs.getLong("datatype");
                    String p = rs.getString("tpschema");
                    String n = rs.getString("typename");
                    LogicalDatatypeInfMsg m = LogicalDatatypeInfMsg.of(d, p, n);
                    r = MainlineRecordNormal.of(0L, m);
                } else if (config instanceof MainlineConfigSnapshot) {
                    metric.statusInfor = "send txation finish record, snapshot starts.";
                    MainlineMetricSnapshot ssmetric //
                        = MainlineMetricSnapshot.of(metric.startMillis);
                    ssmetric.recordCount = this.metric.recordCount;
                    ssmetric.fetchCounts = this.metric.fetchCounts;
                    ssmetric.fetchMillis = this.metric.fetchMillis;
                    ssmetric.offerCounts = this.metric.offerCounts;
                    ssmetric.offerMillis = this.metric.offerMillis;
                    ssmetric.logDatetime = this.metric.logDatetime;
                    ssmetric.statusInfor = this.metric.statusInfor;
                    return MainlineActionNormalSnapshot.of((MainlineConfigSnapshot) config, sender, ssmetric);
                }
                else {
                    metric.statusInfor = "send txation finish record, txaction starts.";
                    MainlineMetricTxaction txmetric //
                        = MainlineMetricTxaction.of(metric.startMillis);
                    txmetric.recordCount = this.metric.recordCount;
                    txmetric.fetchCounts = this.metric.fetchCounts;
                    txmetric.fetchMillis = this.metric.fetchMillis;
                    txmetric.offerCounts = this.metric.offerCounts;
                    txmetric.offerMillis = this.metric.offerMillis;
                    txmetric.logDatetime = this.metric.logDatetime;
                    txmetric.statusInfor = this.metric.statusInfor;
                    return MainlineActionNormalTxaction.of((MainlineConfigTxaction)config, sender, txmetric);
                }
            }
            metric.statusInfor = "upper consumer end when query data types";
            return MainlineActionFinish.of(config, sender, metric);
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

    @Override
    MainlineMetric getMetric()
    {
        return this.metric;
    }
}
