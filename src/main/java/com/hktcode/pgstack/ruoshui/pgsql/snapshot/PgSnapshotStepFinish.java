/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 获取快照结束.
 *
 * @param <M> 获取快照时的metric类.
 */
public interface PgSnapshotStepFinish<M> extends PgSnapshotStep<M>
{
    /**
     * 获取快照结束时的动作.
     *
     * @param pgdata 获取快照的连接.
     *
     * @return {@code true}表示快照正常结束，{@code false}表示获取快照出错.
     *
     * @throws SQLException 如果执行提交或者回滚时出现错误.
     */
    boolean finish(Connection pgdata) throws SQLException;
}
