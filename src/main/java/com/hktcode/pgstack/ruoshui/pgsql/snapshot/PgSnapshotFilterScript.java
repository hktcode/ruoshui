/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import com.fasterxml.jackson.databind.JsonNode;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PgReplRelation;
import com.hktcode.pgstack.ruoshui.script.BasicScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;

/**
 * 通过外部提供的脚本实现过滤.
 */
public class PgSnapshotFilterScript implements PgSnapshotFilter
{
    private static final Logger logger = LoggerFactory.getLogger(PgSnapshotFilterScript.class);

    /**
     * 根据json串获取{@link PgSnapshotFilterScript}对象.
     *
     * @param json 表示json字符串的{@link JsonNode}对象.
     *
     * @return 根据{@code json}构建的{@link PgSnapshotFilterScript}对象.
     *
     * @throws ScriptException 如果构建脚本时抛出异常.
     * @throws ArgumentNullException if {@code json} is {@code null}.
     */
    public static PgSnapshotFilterScript of(JsonNode json) //
        throws ScriptException
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        BasicScript script = BasicScript.ofJsonObject(json);
        return new PgSnapshotFilterScript(script);
    }

    /**
     * 内部脚本对象.
     */
    private final BasicScript script;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean eval(PgReplRelation relation) throws ScriptException
    {
        Object result = script.eval(relation);
        if (result instanceof Boolean) {
            return (boolean)result;
        }
        logger.warn("eval doesn't return a boolean: result={}, class={}", result, result.getClass());
        return false;
    }

    /**
     * constructor.
     *
     * @param script 内部脚本对象.
     */
    private PgSnapshotFilterScript(BasicScript script)
    {
        this.script = script;
    }
}
