/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.ruoshui.reciever.pgsql.entity;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PgReplAttribute;

/**
 * 关系的一个属性.
 */
public class PgsqlAttribute
{
    /**
     * 从{@code PgReplAttribute}对象中构建{@code PgsqlAttribute}.
     *
     * @param attrinfo 属性信息.
     * @throws ArgumentNullException 如果{@code attrinfo}是{@code null}.
     * @return 根据{@code PgReplAttribute}对象构建的{@code PgsqlAttribute}对象.
     */
    public static PgsqlAttribute of(PgReplAttribute attrinfo)
    {
        if (attrinfo == null) {
            throw new ArgumentNullException("attrinfo");
        }
        return new PgsqlAttribute //
            /* */( attrinfo.attrname //
            /* */, attrinfo.tpschema //
            /* */, attrinfo.typename //
            /* */, attrinfo.attflags //
            /* */, attrinfo.datatype //
            /* */, attrinfo.attypmod //
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
     * @param attflags 标记信息.
     * @param datatype 类型标识oid.
     * @param attypmod 属性的{@code attypmod}.
     */
    private PgsqlAttribute
        /* */( String attrname
        /* */, String tpschema
        /* */, String typename
        /* */, long attflags
        /* */, long datatype
        /* */, long attypmod
        /* */)
    {
        this.attrname = attrname;
        this.tpschema = tpschema;
        this.typename = typename;
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
        node.put("attypmod", attypmod);
        node.put("attflags", attflags);
    }
}
