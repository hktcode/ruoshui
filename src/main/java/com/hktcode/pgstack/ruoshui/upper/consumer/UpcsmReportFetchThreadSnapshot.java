/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineConfig;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgsenderResult;
import com.hktcode.pgstack.ruoshui.upper.pgsender.SnapshotConfig;

public class UpcsmReportFetchThreadSnapshot extends UpcsmReportFetchThread
{
    public static UpcsmReportFetchThreadSnapshot of //
        /* */( PgsenderResult<MainlineRecord, MainlineConfig> mainline //
        /* */, ImmutableList<PgsenderResult<UpcsmFetchRecordSnapshot, SnapshotConfig>> snapshot //
        /* */)
    {
        if (mainline == null) {
            throw new ArgumentNullException("mainline");
        }
        if (snapshot == null) {
            throw new ArgumentNullException("snapshot");
        }
        return new UpcsmReportFetchThreadSnapshot(mainline, snapshot);
    }

    private UpcsmReportFetchThreadSnapshot //
        /* */( PgsenderResult<MainlineRecord, MainlineConfig> mainline //
        /* */, ImmutableList<PgsenderResult<UpcsmFetchRecordSnapshot, SnapshotConfig>> snapshot //
        /* */)
    {
        super(mainline, snapshot);
    }
}
