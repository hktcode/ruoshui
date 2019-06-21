/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.datatype;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalDatatypeInfMsg;
import com.hktcode.pgjdbc.PostgreSQL;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerConfig;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.ZonedDateTime;

public class UpperDatatype implements Runnable
{
    public static UpperDatatype of //
        /* */( UpperConsumerConfig config //
        /* */, PgConnection pgrepl //
        /* */, UpperDatatypeSender sender //
        /* */) //
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (pgrepl == null) {
            throw new ArgumentNullException("pgrepl");
        }
        if (sender == null) {
            throw new ArgumentNullException("sender");
        }
        return new UpperDatatype(config, pgrepl, sender);
    }

    private static final Logger logger = LoggerFactory.getLogger(UpperDatatype.class);

    private final UpperConsumerConfig config;

    private final PgConnection pgrepl;

    private final UpperDatatypeSender sender;

    private UpperDatatype //
        /* */( UpperConsumerConfig config //
        /* */, PgConnection pgrepl //
        /* */, UpperDatatypeSender sender //
        /* */) //
    {
        this.config = config;
        this.pgrepl = pgrepl;
        this.sender = sender;
    }

    public void run()
    {
        ZonedDateTime createTime = ZonedDateTime.now();
        UpperDatatypeMetric metric = UpperDatatypeMetric.of(createTime);
        UpperDatatypeRecord r = null;
        metric.statusInfor = "query data types";
        final int fetchSize = 128;
        try (Connection c = this.config.srcProperty.queriesConnection();
             Statement s = PostgreSQL.createStatement(c, fetchSize);
             ResultSet rs = s.executeQuery(TYPES_SELECT)) {
            rs.setFetchDirection(ResultSet.FETCH_FORWARD);
            rs.setFetchSize(fetchSize);
            while (true) {
                long timeout = this.config.logDuration;
                if (sender.isDone()) {
                    metric.statusInfor = "upper consumer end when query data types";
                    return;
                } else if (r != null) {
                    r = this.sender.push(r, timeout, timeout, metric);
                } else if (rs.next()) {
                    long d = rs.getLong("datatype");
                    String p = rs.getString("tpschema");
                    String n = rs.getString("typename");
                    LogicalDatatypeInfMsg m = LogicalDatatypeInfMsg.of(d, p, n);
                    r = UpperDatatypeRecordNormal.of(0L, m);
                } else {
                    break;
                }
            }
            long timeout = this.config.logDuration;
            metric.statusInfor = "send txation finish record.";
            r = UpperDatatypeRecordFinish.of(config, pgrepl, sender.status);
            this.sender.send(r, timeout, timeout, metric);
        }
        catch (InterruptedException ex) {
            logger.error("should not be interrupted by other thread.");
            Thread.currentThread().interrupt();
        }
        catch (Exception ex) {
            logger.info("throws exception: ", ex);
            ZonedDateTime endtime = ZonedDateTime.now();
            metric.statusInfor = "throw exception: " + ex.getMessage();
            r = UpperDatatypeRecordThrows.of(endtime, ex);
            long timeout = this.config.logDuration;
            try {
                this.sender.send(r, timeout, timeout, metric);
            }
            catch (InterruptedException e) {
                logger.error("should not be interrupted by other thread.");
                Thread.currentThread().interrupt();
            }
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
}
