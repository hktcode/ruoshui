/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Phaser;

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

    public void waiting() throws InterruptedException
    {
        int phase = this.phaser.arriveAndDeregister();
        if (phase < 0) {
            logger.error("wait arriveAndDeregister: phaser={}", phaser);
        }
        phase = this.phaser.awaitAdvanceInterruptibly(phase);
        if (phase < 0) {
            logger.error("wait awaitAdvanceInterruptibly 0: phaser={}", phaser);
        }
        phase = this.phaser.awaitAdvanceInterruptibly(phase);
        if (phase >= 0) {
            logger.error("wait awaitAdvanceInterruptibly 1: phaser={}", phaser);
        }
    }

    public void acquire() throws InterruptedException
    {
        int phase = this.phaser.arrive();
        if (phase < 0) {
            logger.error("acq arrive: phaser={}", phase);
        }
        phase = this.phaser.awaitAdvanceInterruptibly(phase);
        if (phase < 0) {
            logger.error("acq awaitAdvanceInterruptibly: phaser={}", phase);
        }
    }

    public void release() throws InterruptedException
    {
        int phase = this.phaser.arriveAndDeregister();
        if (phase < 0) {
            logger.error("release arriveAndDeregister: phaser={}", phase);
        }
        phase = this.phaser.awaitAdvanceInterruptibly(phase);
        if (phase >= 0) {
            logger.error("release awaitAdvanceInterruptibly: phaser={}", phase);
        }
    }
}
