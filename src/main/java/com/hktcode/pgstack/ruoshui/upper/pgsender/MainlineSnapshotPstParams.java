/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.bgsimple.method.SimpleMethodPstParams;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineSnapshotPstParams implements SimpleMethodPstParams<PgAction>
{
    public static MainlineSnapshotPstParams of(PgConfigSnapshot config)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        return new MainlineSnapshotPstParams(config);
    }

    @Override
    public PgResult run(PgAction action)
        throws InterruptedException
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return action.pst(config);
    }

    private final PgConfigSnapshot config;

    private MainlineSnapshotPstParams(PgConfigSnapshot config)
    {
        this.config = config;
    }
}
