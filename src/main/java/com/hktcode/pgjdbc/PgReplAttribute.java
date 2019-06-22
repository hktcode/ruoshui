/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgjdbc;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

/**
 * 关系的一个属性.
 */
public class PgReplAttribute
{
    /**
     * {@code of}函数.
     *
     * @param attrname 属性名称.
     * @param tpschema 属性类型所在schema.
     * @param typename 类型名称.
     * @param nullable 该属性是否可以为{@code null}
     * @param attflags 标记信息.
     * @param datatype 类型标识oid.
     * @param attypmod 属性的{@code attypmod}.
     */
    public static PgReplAttribute of //
        /* */( String attrname //
        /* */, String tpschema //
        /* */, String typename //
        /* */, long nullable //
        /* */, long attflags //
        /* */, long datatype //
        /* */, long attypmod //
        /* */)
    {
        if (attrname == null) {
            throw new ArgumentNullException("attrname");
        }
        if (tpschema == null) {
            throw new ArgumentNullException("tpschema");
        }
        if (typename == null) {
            throw new ArgumentNullException("typename");
        }
        return new PgReplAttribute //
            /* */( attrname //
            /* */, tpschema //
            /* */, typename //
            /* */, nullable //
            /* */, attflags //
            /* */, datatype //
            /* */, attypmod //
            /* */);
    }

    /**
     * 属性名称.
     */
    public final String attrname;

    /**
     * 类型所在的schema.
     */
    public final String tpschema;

    /**
     * 类型名称.
     */
    public final String typename;

    /**
     * 该属性是否可以为{@code NULL}.
     *
     * <dl>
     *     <dt>{@code 0}</dt><dd>不能为{@code null}</dd>
     *     <dt>{@code 1}</dt><dd>可以为{@code null}</dd>
     *     <dt>{@code -1}</dt><dd>此信息丢失</dd>
     * </dl>
     */
    public final long nullable;

    /**
     * 标识信息.
     */
    public final long attflags;

    /**
     * 属性的类型标识.
     */
    public final long datatype;

    /**
     * 属性的{@code atttypmod}信息.
     */
    public final long attypmod;

    /**
     * 构造函数.
     *
     * @param attrname 属性名称.
     * @param tpschema 属性类型所在schema.
     * @param typename 类型名称.
     * @param nullable 该属性是否可以为{@code null}
     * @param attflags 标记信息.
     * @param datatype 类型标识oid.
     * @param attypmod 属性的{@code attypmod}.
     */
    private PgReplAttribute
        /* */( String attrname
        /* */, String tpschema
        /* */, String typename
        /* */, long nullable
        /* */, long attflags
        /* */, long datatype
        /* */, long attypmod
        /* */)
    {
        this.attrname = attrname;
        this.tpschema = tpschema;
        this.typename = typename;
        this.nullable = nullable;
        this.attflags = attflags;
        this.datatype = datatype;
        this.attypmod = attypmod;
    }

    /**
     * 将{@code PgsqlAttribute}特性形式的字符串添加到{@code builder}后面.
     *
     * 一般用于生成{@link #toString()}字符串中人类可读的形式.
     *
     * @param builder 要添加的{@code StringBuilder}对象.
     */
    public void appendTo(StringBuilder builder)
    {
        builder.append(attrname);
        builder.append('|');
        builder.append(tpschema);
        builder.append('|');
        builder.append(typename);
        builder.append('|');
        builder.append(nullable);
        builder.append('|');
        builder.append(attflags);
        builder.append('|');
        builder.append(datatype);
        builder.append('|');
        builder.append(attypmod);
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

    /**
     * 设置{@link ObjectNode}的属性.
     *
     * @param node 要设置的{@link ObjectNode}对象.
     */
    public void putTo(ObjectNode node)
    {
        node.put("attrname", attrname);
        node.put("tpschema", tpschema);
        node.put("typename", typename);
        node.put("datatype", datatype);
        node.put("nullable", nullable);
        node.put("attypmod", attypmod);
        node.put("attflags", attflags);
    }
}
