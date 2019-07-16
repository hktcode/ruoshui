/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.status.SimpleStatusInnerEnd;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshot;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotConfig;
import com.hktcode.pgstack.ruoshui.upper.snapshot.SnapshotMetric;
import org.postgresql.jdbc.PgConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZonedDateTime;

class PgsqlTxactionMetricNormalSnapshot extends PgsqlTxactionMetricNormal
{
    static PgsqlTxactionMetricNormalSnapshot of(MainlineConfigSnapshot config, PgsqlTxactionMetricDatatype metric)
    {
        return new PgsqlTxactionMetricNormalSnapshot(config, metric);
    }

    private PgsqlTxactionMetricNormalSnapshot(MainlineConfigSnapshot config, PgsqlTxactionMetricDatatype metric)
    {
        super(metric.startMillis);
        this.config = config;
        this.recordCount = metric.recordCount;
        this.fetchCounts = metric.fetchCounts;
        this.fetchMillis = metric.fetchMillis;
        this.offerCounts = metric.offerCounts;
        this.offerMillis = metric.offerMillis;
        this.logDatetime = metric.logDatetime;
        this.statusInfor = metric.statusInfor;
    }

    private final MainlineConfigSnapshot config;

    @Override
    PgsqlTxactionMetric next(PgConnection pgrepl, PgsqlTxaction worker) //
        throws SQLException, InterruptedException
    {
        PgSnapshotConfig pgconfig = config.iniSnapshot;
        try (Connection data = config.srcProperty.queriesConnection()) {
            PgConnection pgdata = data.unwrap(PgConnection.class);
            PgSnapshot<SnapshotMetric> runnable = PgSnapshot.of(pgconfig, pgrepl, pgdata, null);
            Thread t = new Thread(runnable);
            t.start();
            while (!(worker.newStatus(worker, this) instanceof SimpleStatusInnerEnd)) {
                if (!t.isAlive()) {
                    return PgsqlTxactionMetricNormalTxaction.of(config, this);
                }
                t.join(this.config.waitTimeout);
            }
            t.interrupt();
            pgdata.cancelQuery();
            pgrepl.cancelQuery();
        }
        ZonedDateTime attime = ZonedDateTime.now();
        return PgsqlTxactionMetricFinish.of(config, this, attime);
    }
}
