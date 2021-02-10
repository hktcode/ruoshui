package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.hktcode.simple.*;
import com.hktcode.simple.SimplePhaserOuter;
import com.hktcode.lang.exception.ArgumentNullException;

public abstract class UpperAction extends SimpleActionRun<UpperEntity>
{
    protected UpperAction(SimpleHolder<UpperEntity> holder)
    {
        super(holder);
    }

    @Override
    public SimpleAction<UpperEntity> next(Throwable throwError, SimpleMetric metric)
            throws InterruptedException
    {
        if (throwError == null) {
            throw new ArgumentNullException("throwError");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        metric.throwErrors.add(throwError);
        metric.endDatetime = System.currentTimeMillis();
        SimplePhaserOuter del = SimplePhaserOuter.of(3);
        while (this.holder.run(metric).deletets == Long.MAX_VALUE) {
            this.holder.cmd(del, UpperEntity::end);
        }
        return SimpleActionEnd.of(this.holder);
    }
}
