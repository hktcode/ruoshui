/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.BgWorker;
import com.hktcode.bgsimple.method.*;
import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Phaser;

public class SimpleStatusCmd implements SimpleStatus
{
    public static SimpleStatusCmd of(Phaser phaser, SimpleMethod[] method)
    {
        if (phaser == null) {
            throw new ArgumentNullException("phaser");
        }
        if (method == null) {
            throw new ArgumentNullException("method");
        }
        return new SimpleStatusCmd(phaser, method);
    }

    private static final Logger logger = LoggerFactory.getLogger(SimpleStatusCmd.class);

    public final Phaser phaser;

    public final SimpleMethod[] method;

    private SimpleStatusCmd(Phaser phaser, SimpleMethod[] method)
    {
        this.phaser = phaser;
        this.method = method;
    }

    public SimpleStatus run(BgWorker wkstep, int number) //
        throws InterruptedException
    {
        if (wkstep == null) {
            throw new ArgumentNullException("wkstep");
        }
        this.method[number] = this.method[number].run(wkstep);
        int phase = this.phaser.arriveAndDeregister();
        if (phase < 0) {
            logger.error("phaser.arriveAndDeregister: number={}, phaser={}", number, phase);
        }
        this.phaser.awaitAdvanceInterruptibly(phase);
        int endcount = 0;
        for (SimpleMethod simpleMethod : this.method) {
            if (simpleMethod instanceof SimpleMethodResultEnd) {
                ++endcount;
            }
        }
        SimpleStatus result;
        if (endcount == 0) {
            result = SimpleStatusRun.of();
        } else if (endcount < 3)  {
            SimpleMethod[] del = new SimpleMethod[]{
                    SimpleMethodParamsDelDefault.of(),
                    SimpleMethodParamsDelDefault.of(),
                    SimpleMethodParamsDelDefault.of()
            };
            result = SimpleStatusCmd.of(new Phaser(3), del);
        } else {
            SimpleMethodResultEnd[] end = new SimpleMethodResultEnd[this.method.length];
            for (int i = 0; i < this.method.length; ++i) {
                end[i] = (SimpleMethodResultEnd)this.method[i];
            }
            result = SimpleStatusEnd.of(ImmutableList.copyOf(end));
        }
        return result;
    }

    public SimpleStatusCmd cmd(SimpleStatusCmd cmd)
    {
        if (cmd == null) {
            throw new ArgumentNullException("cmd");
        }
        return this;
    }

    public ImmutableList<SimpleMethodResult> inner() throws InterruptedException
    {
        phaser.awaitAdvanceInterruptibly(phaser.getPhase());
        SimpleMethodResult[] result = new SimpleMethodResult[] {
                (SimpleMethodResult) this.method[0],
                (SimpleMethodResult) this.method[1],
                (SimpleMethodResult) this.method[2],
        };
        return ImmutableList.copyOf(result);
    }
}
