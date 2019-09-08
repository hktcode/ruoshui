/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.method.SimpleMethodPstParams;
import com.hktcode.bgsimple.method.SimpleMethodPstResult;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmAction;
import com.hktcode.pgstack.ruoshui.upper.snapshot.SnapshotConfig;

import javax.script.ScriptException;

public class MainlineSnapshotPstParams implements SimpleMethodPstParams<MainlineAction>
{
    public static MainlineSnapshotPstParams of(SnapshotConfig config)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        return new MainlineSnapshotPstParams(config);
    }

    @Override
    @SuppressWarnings("unchecked")
    public MainlineResult run(MainlineAction action)
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
