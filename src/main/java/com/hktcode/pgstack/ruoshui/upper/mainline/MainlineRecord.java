/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.pgstack.ruoshui.upper.UpperConsumerMetric;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;

interface MainlineRecord
{
    UpperConsumerRecord toUpcsmRecord();
}
