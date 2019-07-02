/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgjdbc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

/**
 * 记录中的元组完整信息.
 */
public class PgReplComponent
{
    /**
     * 属性信息.
     */
    public final PgReplAttribute attrinfo;

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
     * 工厂函数.
     *
     * @param attrinfo 属性信息.
     * @param oldvalue 旧值.
     * @param newvalue 新值.
     */
    public static PgReplComponent of //
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
        return new PgReplComponent(attrinfo, oldvalue, newvalue);
    }

    /**
     * 构造函数.
     *
     * @param attrinfo 属性名称.
     * @param oldvalue 旧值.
     * @param newvalue 新值.
     */
    private PgReplComponent //
        /* */( PgReplAttribute attrinfo //
        /* */, JsonNode oldvalue //
        /* */, JsonNode newvalue //
        /* */)
    {
        this.attrinfo = attrinfo;
        this.oldvalue = oldvalue;
        this.newvalue = newvalue;
    }

    /**
     * 将{@code PgsqlComponent}特性形式的字符串添加到{@code builder}后面.
     *
     * 一般用于生成{@link #toString()}字符串中人类可读的形式.
     *
     * @param builder 要添加的{@code StringBuilder}对象.
     */
    public void appendTo(StringBuilder builder)
    {
        builder.append(attrinfo.attrname);
        builder.append('|');
        builder.append(attrinfo.tpschema);
        builder.append('|');
        builder.append(attrinfo.typename);
        builder.append('|');
        this.appendJsonNodeTo(oldvalue, builder);
        builder.append('|');
        this.appendJsonNodeTo(newvalue, builder);
        builder.append('|');
        builder.append(attrinfo.datatype);
        builder.append('|');
        builder.append(attrinfo.nullable);
        builder.append('|');
        builder.append(attrinfo.attypmod);
        builder.append('|');
        builder.append(attrinfo.attflags);
    }

    /**
     * 设置{@link ObjectNode}的属性.
     *
     * @param node 要设置的{@link ObjectNode}对象.
     */
    public void putTo(ObjectNode node)
    {
        node.put("attrname", attrinfo.attrname);
        node.put("tpschema", attrinfo.tpschema);
        node.put("typename", attrinfo.typename);
        if (!(oldvalue instanceof MissingNode)) {
            node.set("oldvalue", oldvalue);
        }
        if (!(newvalue instanceof MissingNode)) {
            node.set("newvalue", newvalue);
        }
        node.put("datatype", attrinfo.datatype);
        node.put("nullable", attrinfo.nullable);
        node.put("attypmod" ,attrinfo.attrname);
        node.put("attflags" ,attrinfo.attflags);
    }

    /**
     * 将{@code JsonNode}转换成特定的形式附加到{@code StringBuilder}后面.
     *
     * @param json 要转换的{@code JsonNode}对象.
     * @param builder 附加的{@code StringBuilder}对象.
     */
    private void appendJsonNodeTo(JsonNode json, StringBuilder builder)
    {
        if (json == null || json instanceof NullNode) {
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
