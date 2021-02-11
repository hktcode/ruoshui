/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.simple;

import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Phaser;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class SimplePhaserOuter implements SimplePhaser
{
    public static SimplePhaserOuter of(int parties)
    {
        return new SimplePhaserOuter(parties);
    }

    private static final Logger logger = LoggerFactory.getLogger(SimplePhaserOuter.class);

    private final Phaser phaser;

    private SimplePhaserOuter(int parties)
    {
        this.phaser = new Phaser(parties);
    }

    public void run() throws InterruptedException
    {
        int phase = this.phaser.arriveAndDeregister();
        if (phase < 0) {
            logger.error("phaser.arriveAndDeregister: phaser={}", phase);
        }
        phase = this.phaser.awaitAdvanceInterruptibly(phase);
        if (phase < 0) {
            logger.error("phaser.awaitAdvanceInterruptibly: phaser={}", phase);
        }
        phase = this.phaser.awaitAdvanceInterruptibly(phase);
        if (phase >= 0) {
            logger.error("phaser.awaitAdvanceInterruptibly: phaser={}", phase);
        }
    }

    public <R extends SimpleResult> //
    R run(SimplePhaserInner origin, BiFunction<SimplePhaserInner, SimplePhaserOuter, R> keeper)
            throws InterruptedException
    {
        if (keeper == null) {
            throw new ArgumentNullException("keeper");
        }
        if (origin == null) {
            throw new ArgumentNullException("origin");
        }
        int phase = this.phaser.arrive();
        if (phase < 0) {
            logger.error("phaser.arrive: phaser={}", phase);
        }
        phase = this.phaser.awaitAdvanceInterruptibly(phase);
        if (phase < 0) {
            logger.error("phaser.awaitAdvanceInterruptibly: phaser={}", phase);
        }
        try {
            return keeper.apply(origin, this);
        }
        finally {
            phase = this.phaser.arriveAndDeregister();
            if (phase < 0) {
                logger.error("phaser.arriveAndDeregister: phase={}", phase);
            }
            phase = this.phaser.awaitAdvanceInterruptibly(phase);
            if (phase >= 0) {
                logger.error("phaser.awaitAvanceInteruptible: phase={}", phase);
            }
        }
    }


    public <R extends SimpleResult> R run(Supplier<R> keeper)
            throws InterruptedException
    {
        if (keeper == null) {
            throw new ArgumentNullException("keeper");
        }
        int phase = this.phaser.arrive();
        if (phase < 0) {
            logger.error("phaser.arrive: phaser={}", phase);
        }
        phase = this.phaser.awaitAdvanceInterruptibly(phase);
        if (phase < 0) {
            logger.error("phaser.awaitAdvanceInterruptibly: phaser={}", phase);
        }
        try {
            return keeper.get();
        }
        finally {
            phase = this.phaser.arriveAndDeregister();
            if (phase < 0) {
                logger.error("phaser.arriveAndDeregister: phase={}", phase);
            }
            phase = this.phaser.awaitAdvanceInterruptibly(phase);
            if (phase >= 0) {
                logger.error("phaser.awaitAvanceInteruptible: phase={}", phase);
            }
        }
    }
}
