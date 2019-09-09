/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.method.SimpleMethodPstParams;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.consumer.MainlineRecord;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgsenderAction;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgsenderResult;
import com.hktcode.pgstack.ruoshui.upper.pgsender.SnapshotConfig;

public class MainlineSnapshotPstParams implements SimpleMethodPstParams<PgsenderAction<MainlineRecord, MainlineConfig>>
{
    public static MainlineSnapshotPstParams of(SnapshotConfig config)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        return new MainlineSnapshotPstParams(config);
    }

    @Override
    public PgsenderResult<MainlineRecord, MainlineConfig> run(PgsenderAction<MainlineRecord, MainlineConfig> action)
        throws InterruptedException
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return action.pst(config);
    }

    private final SnapshotConfig config;

    private MainlineSnapshotPstParams(SnapshotConfig config)
    {
        this.config = config;
    }
}
