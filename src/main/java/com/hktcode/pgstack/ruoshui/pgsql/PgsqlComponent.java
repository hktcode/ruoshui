/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.pgsql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

/**
 * 记录中的元组信息.
 */
public abstract class PgsqlComponent
{
    /**
     * 属性名称.
     */
    public final String attrname;

    /**
     * 属性类型所在schema.
     */
    public final String tpschema;

    /**
     * 属性类型名称.
     */
    public final String typename;

    /**
     * 属性类型标识oid.
     */
    public final long datatype;

    /**
     * 属性的{@code atttypmod}信息.
     */
    public final long attypmod;

    /**
     * 标识信息.
     */
    public final long attflags;

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
    protected PgsqlComponent //
        /* */( String attrname //
        /* */, String tpschema //
        /* */, String typename //
        /* */, long datatype //
        /* */, long attypmod //
        /* */, long attflags //
        /* */)
    {
        this.attrname = attrname;
        this.tpschema = tpschema;
        this.typename = typename;
        this.datatype = datatype;
        this.attypmod = attypmod;
        this.attflags = attflags;
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
        builder.append(attrname);
        builder.append('|');
        builder.append(tpschema);
        builder.append('|');
        builder.append(typename);
        builder.append('|');
        builder.append(datatype);
        builder.append('|');
        builder.append(attypmod);
        builder.append('|');
        builder.append(attflags);
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
        node.put("attrname", attrname);
        node.put("tpschema", tpschema);
        node.put("typename", typename);
        node.put("datatype", datatype);
        node.put("attypmod", attypmod);
        node.put("attflags", attflags);
    }

    /**
     * 将{@code JsonNode}转换成特定的形式附加到{@code StringBuilder}后面.
     *
     * @param json 要转换的{@code JsonNode}对象.
     * @param builder 附加的{@code StringBuilder}对象.
     */
    protected static void appendJsonNodeTo(JsonNode json, StringBuilder builder)
    {
        if (json instanceof NullNode) {
            builder.append("(null)");
        }
        else if (json instanceof MissingNode) {
            builder.append("(missing)");
        }
        else {
            builder.append(json.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        this.appendTo(builder);
        return builder.toString();
    }
}
