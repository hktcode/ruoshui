/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PgReplRelation;

import javax.script.ScriptException;


/**
 * 快照关系过滤器.
 */
public interface PgSnapshotFilter
{
    /**
     * 过滤指定关系.
     *
     * @param relation 关系信息.
     * @return 如果{@code true}，则在快照中会查询{@code relation}，否则，不会查询.
     *
     * @throws ScriptException 当使用脚本出现错误时抛出.
     * @throws ArgumentNullException if {@code relation} is {@code null}.
     */
    boolean eval(PgReplRelation relation) throws ScriptException;
}
