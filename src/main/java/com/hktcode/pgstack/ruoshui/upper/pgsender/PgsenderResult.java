/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.bgsimple.method.SimpleMethodAllResult;

public interface PgsenderResult<R, C extends PgsenderConfig<R, C>> //
    extends SimpleMethodAllResult<PgsenderAction<R, C>>
{
}
