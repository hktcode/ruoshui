/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.BgWorker;
import com.hktcode.bgsimple.method.*;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.Phaser;

public class SimpleStatusEnd implements SimpleStatus
{
    public static SimpleStatusEnd of(ImmutableList<SimpleMethodResultEnd> result)
    {
        if (result == null) {
            throw new ArgumentNullException("result");
        }
        return new SimpleStatusEnd(result);
    }

    public final ImmutableList<SimpleMethodResultEnd> result;

    private SimpleStatusEnd(ImmutableList<SimpleMethodResultEnd> result)
    {
        this.result = result;
    }

    @Override
    public SimpleStatusCmd cmd(SimpleStatusCmd cmd)
    {
        if (cmd == null) {
            throw new ArgumentNullException("cmd");
        }
        // TODO: 检查pst中的Phaser是否已经终止.
        SimpleMethod[] method = new SimpleMethod[result.size()];
        result.toArray(method);
        return SimpleStatusCmd.of(new Phaser(0), method);
    }

    @Override
    public SimpleStatusEnd run(BgWorker wkstep, int number)
    {
        if (wkstep == null) {
            throw new ArgumentNullException("wkstep");
        }
        return this;
    }
}
