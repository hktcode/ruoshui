/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

public class PgsqlSnapshotRecordPauseWorld implements PgsqlSnapshotRecord
{
    public static PgsqlSnapshotRecordPauseWorld of()
    {
        return new PgsqlSnapshotRecordPauseWorld();
    }

    private PgsqlSnapshotRecordPauseWorld()
    {
    }
}
