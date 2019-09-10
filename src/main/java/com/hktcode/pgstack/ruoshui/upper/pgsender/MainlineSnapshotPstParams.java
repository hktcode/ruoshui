/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.bgsimple.method.SimpleMethodPstParams;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineSnapshotPstParams implements SimpleMethodPstParams<PgsenderAction<MainlineConfig>>
{
    public static MainlineSnapshotPstParams of(SnapshotConfig config)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        return new MainlineSnapshotPstParams(config);
    }

    @Override
    public PgsenderResult<MainlineConfig> run(PgsenderAction<MainlineConfig> action)
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
