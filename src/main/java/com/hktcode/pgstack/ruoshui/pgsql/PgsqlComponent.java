/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.pgsql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PgReplAttribute;
import com.hktcode.pgjdbc.PgReplComponent;

/**
 * 记录中的元组完整信息.
 */
public class PgsqlComponent
{
    /**
     * 根据{@link PgReplComponent}构建{@link PgsqlComponent}.
     *
     * @param component 指定的{@link PgReplComponent}对象.
     * @return 根据{@code component}构建的{@link PgsqlComponent}对象.
     * @throws ArgumentNullException if {@code component} is {@code null}.
     */
    public static PgsqlComponent of(PgReplComponent component)
    {
        if (component == null) {
            throw new ArgumentNullException("component");
        }
        return new PgsqlComponent //
            /* */( component.attrinfo.attrname //
            /* */, component.attrinfo.tpschema //
            /* */, component.attrinfo.typename //
            /* */, component.attrinfo.datatype //
            /* */, component.attrinfo.attypmod //
            /* */, component.attrinfo.attflags //
            /* */, component.oldvalue //
            /* */, component.newvalue //
            /* */);
    }

    /**
     * 根据属性信息、旧值和新值构建{@link PgsqlComponent}对象.
     *
     * @param attrinfo 属性信息.
     * @param oldvalue 旧值.
     * @param newvalue 新值.
     * @return 根据{@code attrinfo}、{@code oldvalue}和{@code newvalue}构建的{@link PgsqlComponent}对象.
     * @throws ArgumentNullException if {@code attrinfo}, {@code oldvalue} or {@code newvalue} is {@code null}.
     */
    public static PgsqlComponent of //
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
        return new PgsqlComponent //
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
    private PgsqlComponent //
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
        this.attrname = attrname;
        this.tpschema = tpschema;
        this.typename = typename;
        this.datatype = datatype;
        this.attypmod = attypmod;
        this.attflags = attflags;
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
        builder.append(attrname);
        builder.append('|');
        builder.append(tpschema);
        builder.append('|');
        builder.append(typename);
        builder.append('|');
        this.appendJsonNodeTo(oldvalue, builder);
        builder.append('|');
        this.appendJsonNodeTo(newvalue, builder);
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
        if (!(oldvalue instanceof MissingNode)) {
            node.set("oldvalue", oldvalue);
        }
        if (!(newvalue instanceof MissingNode)) {
            node.set("newvalue", newvalue);
        }
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
    private void appendJsonNodeTo(JsonNode json, StringBuilder builder)
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
