/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import javax.script.ScriptException;
import java.sql.SQLException;

/**
 * 快照中间执行步骤.
 *
 * @param <M> 执行快照时的metric对象.
 */
public interface PgSnapshotStepProcess<M> extends PgSnapshotStep<M>
{
    /**
     * 获取下一个步骤信息.
     *
     * @param metric 快照的metric对象.
     * @param sender 信息发送器.
     * @return 下一个步骤信息.
     *
     * @throws SQLException 执行SQL语句出现错误时抛出.
     * @throws ScriptException 过滤关系时出现错误是抛出.
     * @throws InterruptedException 被其他线程打断时抛出.
     */
    PgSnapshotStep<M> next(M metric, PgSnapshotSender<M> sender) //
        throws SQLException, ScriptException, InterruptedException;
}
