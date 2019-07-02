package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;

interface MainlineRecord
{
    UpperConsumerRecord update(UpperConsumerMutableMetric metric);
}
