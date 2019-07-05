/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper;

import com.hktcode.bgsimple.method.SimpleMethodAllResult;
import com.hktcode.bgsimple.method.SimpleMethodPst;
import com.hktcode.bgsimple.method.SimpleMethodPstResult;
import com.hktcode.bgsimple.triple.TripleConsumerMetric;
import com.hktcode.bgsimple.triple.TripleMethodResult;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMetric;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineConfig;
import org.postgresql.replication.LogSequenceNumber;

public class UpperMethodPstParamsRecvLsn
    implements SimpleMethodPst<UpperConsumer, UpperConsumerMetric>
{
    public static UpperMethodPstParamsRecvLsn of(LogSequenceNumber receiveLsn)
    {
        if (receiveLsn == null) {
            throw new ArgumentNullException("receiveLsn");
        }
        return new UpperMethodPstParamsRecvLsn(receiveLsn);
    }

    private final LogSequenceNumber receiveLsn;

    private UpperMethodPstParamsRecvLsn(LogSequenceNumber receiveLsn)
    {
        this.receiveLsn = receiveLsn;
    }

    @Override
    public SimpleMethodPstResult<UpperConsumer, UpperConsumerMetric> run(UpperConsumer worker, UpperConsumerMetric metric)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return worker.pst(this.receiveLsn, metric);
    }
}
