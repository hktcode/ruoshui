/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.pgsql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PgReplAttribute;

/**
 * 记录中的元组完整信息.
 */
public class PgsqlComponentAll extends PgsqlComponent
{
    /**
     * 根据属性信息、旧值和新值构建{@link PgsqlComponentAll}对象.
     *
     * @param attrinfo 属性信息.
     * @param oldvalue 旧值.
     * @param newvalue 新值.
     * @return 根据{@code attrinfo}、{@code oldvalue}和{@code newvalue}构建的{@link PgsqlComponentAll}对象.
     * @throws ArgumentNullException if {@code attrinfo}, {@code oldvalue} or {@code newvalue} is {@code null}.
     */
    public static PgsqlComponentAll of //
        /* */( PgReplAttribute attrinfo //
        /* */, JsonNode oldvalue //
        /* */, JsonNode newvalue //
        /* */)
    {
        if (attrinfo == null) {
            throw new ArgumentNullException("attrinfo");
        }
        if (oldvalue == null) {
            throw new ArgumentNullException("oldvalue");
        }
        if (newvalue == null) {
            throw new ArgumentNullException("newvalue");
        }
        return new PgsqlComponentAll //
            /* */( attrinfo.attrname //
            /* */, attrinfo.tpschema //
            /* */, attrinfo.typename //
            /* */, attrinfo.datatype //
            /* */, attrinfo.attypmod //
            /* */, attrinfo.attflags //
            /* */, oldvalue //
            /* */, newvalue //
            /* */);
    }

    /**
     * 旧值.
     *
     * 如果没有旧值，则为{@code MissingNode}.
     */
    public final JsonNode oldvalue;

    /**
     * 新值.
     *
     * 如果没有新值，则为{@code MissingNode}
     */
    public final JsonNode newvalue;

    /**
     * 构造函数.
     *
     * @param attrname 属性名称.
     * @param tpschema 属性类型所在schema.
     * @param typename 类型名称.
     * @param attflags 标记信息.
     * @param datatype 类型标识oid.
     * @param attypmod 属性的{@code attypmod}.
     */
    private PgsqlComponentAll //
        /* */( String attrname //
        /* */, String tpschema //
        /* */, String typename //
        /* */, long datatype //
        /* */, long attypmod //
        /* */, long attflags //
        /* */, JsonNode oldvalue //
        /* */, JsonNode newvalue //
        /* */)
    {
        super(attrname, tpschema, typename, datatype, attypmod, attflags);
        this.oldvalue = oldvalue;
        this.newvalue = newvalue;
    }

    /**
     * 将{@code PgsqlComponent}特性形式的字符串添加到{@code builder}后面.
     *
     * 一般用于生成{@link #toString()}字符串中人类可读的形式.
     *
     * @param builder 要添加的{@code StringBuilder}对象.
     * @throws ArgumentNullException if {@code builder} is {@code null}.
     */
    public void appendTo(StringBuilder builder)
    {
        if(builder == null) {
            throw new ArgumentNullException("builder");
        }
        super.appendTo(builder);
        builder.append('|');
        appendJsonNodeTo(oldvalue, builder);
        builder.append('|');
        appendJsonNodeTo(newvalue, builder);
    }

    /**
     * 设置{@link ObjectNode}的属性.
     *
     * @param node 要设置的{@link ObjectNode}对象.
     * @throws ArgumentNullException if {@code node} is {@code null}.
     */
    public void putTo(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        super.putTo(node);
        if (!(oldvalue instanceof MissingNode)) {
            node.set("oldvalue", oldvalue);
        }
        if (!(newvalue instanceof MissingNode)) {
            node.set("newvalue", newvalue);
        }
    }
}
