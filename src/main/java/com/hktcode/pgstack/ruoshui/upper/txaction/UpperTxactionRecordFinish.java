/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.txaction;

import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;

public class UpperTxactionRecordFinish implements UpperTxactionRecord
{
    public static UpperTxactionRecordFinish of()
    {
        return new UpperTxactionRecordFinish();
    }

    @Override
    public UpperConsumerRecord update(UpperConsumerMutableMetric metric)
    {
        return null;
    }

    private UpperTxactionRecordFinish()
    {
    }
}
