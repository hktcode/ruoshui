/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.BgWorker;

interface MainlineAction<W extends MainlineAction<W>> extends BgWorker<W>
{
    MainlineActionThrowsErrors nextThrowErr(Throwable throwsError);

    MainlineMetricEnd toEndMetrics();
}
