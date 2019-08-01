/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.bgsimple.status.SimpleStatus;

import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public interface MainlineAction<W extends MainlineAction<W>> extends BgWorker<W>
{
}
