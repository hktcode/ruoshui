package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshot;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotConfig;
import com.hktcode.pgstack.ruoshui.upper.snapshot.SnapshotMetric;
import org.postgresql.jdbc.PgConnection;

import java.sql.Connection;
import java.sql.SQLException;

class MainlineActionNormalSnapshot extends MainlineActionNormal
{
    static MainlineActionNormalSnapshot of //
    (MainlineConfigSnapshot config, MainlineSender sender, MainlineMetricSnapshot metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (sender == null) {
            throw new ArgumentNullException("sender");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new MainlineActionNormalSnapshot(config, sender, metric);
    }

    private final MainlineConfigSnapshot config;

    private final MainlineMetricSnapshot metric;

    private MainlineActionNormalSnapshot //
    (MainlineConfigSnapshot config, MainlineSender sender, MainlineMetricSnapshot metric)
    {
        super(sender);
        this.config = config;
        this.metric = metric;
    }

    @Override
    MainlineAction next(PgConnection pgrepl) //
        throws SQLException, InterruptedException
    {
        PgSnapshotConfig pgconfig = config.iniSnapshot;
        try (Connection data = config.srcProperty.queriesConnection()) {
            PgConnection pgdata = data.unwrap(PgConnection.class);
            PgSnapshot<SnapshotMetric> runnable //
                = PgSnapshot.of(pgconfig, pgrepl, pgdata, sender);
            Thread t = new Thread(runnable);
            t.start();
            while (!this.sender.isDone()) {
                if (!t.isAlive()) {
                    MainlineMetricTxaction txmetric //
                        = MainlineMetricTxaction.of(metric.startMillis);
                    txmetric.recordCount = this.metric.recordCount;
                    txmetric.fetchCounts = this.metric.fetchCounts;
                    txmetric.fetchMillis = this.metric.fetchMillis;
                    txmetric.offerCounts = this.metric.offerCounts;
                    txmetric.offerMillis = this.metric.offerMillis;
                    txmetric.logDatetime = this.metric.logDatetime;
                    txmetric.statusInfor = this.metric.statusInfor;
                    MainlineConfigTxaction txconfig = MainlineConfigTxaction.of //
                            /* */( this.config.srcProperty //
                            /* */, this.config.logicalRepl //
                            /* */, this.config.waitTimeout //
                            /* */, this.config.logDuration //
                            /* */);
                    return MainlineActionNormalTxaction.of(txconfig, sender, txmetric);
                }
                t.join(this.config.waitTimeout);
            }
            t.interrupt();
            pgdata.cancelQuery();
            pgrepl.cancelQuery();
        }
        return MainlineActionFinish.of(config, sender, metric);
    }

    @Override
    MainlineMetric getMetric()
    {
        return this.metric;
    }
}
