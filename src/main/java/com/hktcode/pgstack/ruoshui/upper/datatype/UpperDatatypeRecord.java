/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.datatype;

import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;

public interface UpperDatatypeRecord
{
    UpperConsumerRecord update(UpperConsumerMutableMetric metric);
}
