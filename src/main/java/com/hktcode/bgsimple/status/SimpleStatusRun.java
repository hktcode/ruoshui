/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.bgsimple.method.SimpleMethod;
import com.hktcode.bgsimple.method.SimpleMethodParamsDelDefault;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.Phaser;

public class SimpleStatusRun implements SimpleStatus
{
    public static SimpleStatusRun of()
    {
        return new SimpleStatusRun();
    }

    private SimpleStatusRun()
    {
    }

    @Override
    public SimpleStatusCmd cmd(SimpleStatusCmd cmd)
    {
        if (cmd == null) {
            throw new ArgumentNullException("cmd");
        }
        // TODO: 检查get的method中不是BasicGetBgResult
        return cmd;
    }

    @Override
    public SimpleStatusCmd run(BgWorker wkstep, int number)
    {
        if (wkstep == null) {
            throw new ArgumentNullException("wkstep");
        }
        Phaser phaser = new Phaser(3);
        SimpleMethod[] method = new SimpleMethod[] {
                SimpleMethodParamsDelDefault.of(),
                SimpleMethodParamsDelDefault.of(),
                SimpleMethodParamsDelDefault.of()
        };
        return SimpleStatusCmd.of(phaser, method);
    }
}
