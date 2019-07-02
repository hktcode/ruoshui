/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.pgsql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentIllegalException;
import com.hktcode.lang.exception.ArgumentNullException;

/**
 * 快照中的元组信息.
 */
public class PgsqlComponentThe extends PgsqlComponent
{
    /**
     * 根据属性名称、属性类型所在schema、类型名称、属性标记信息、类型标识、属性的{@code atttypmod}和元组的值构建{@link PgsqlComponentThe}对象.
     *
     * @param attrname 属性名称.
     * @param tpschema 属性类型所在schema.
     * @param typename 类型名称.
     * @param attflags 标记信息.
     * @param datatype 类型标识oid.
     * @param attypmod 属性的{@code attypmod}.
     * @param thevalue 元组的值.
     * @return 根据{@code attrname}、{@code tpschema}和{@code thevalue}构建的{@link PgsqlComponentThe}对象.
     * @throws ArgumentNullException if {@code attrname}, {@code tpscheam}, {@code typename} or {@code newvalue} is {@code null}.
     */
    public static PgsqlComponentThe of //
        /* */( String attrname //
        /* */, String tpschema //
        /* */, String typename //
        /* */, long datatype //
        /* */, long attypmod //
        /* */, long attflags //
        /* */, JsonNode thevalue //
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
        if (thevalue == null) {
            throw new ArgumentNullException("thevalue");
        }
        if (thevalue instanceof MissingNode) {
            throw new ArgumentIllegalException("value should not be missing", "thevalue", thevalue);
        }
        return new PgsqlComponentThe //
            /* */( attrname //
            /* */, tpschema //
            /* */, typename //
            /* */, datatype //
            /* */, attypmod //
            /* */, attflags //
            /* */, thevalue //
            /* */);
    }

    /**
     * 元组的值.
     */
    public final JsonNode thevalue;

    /**
     * 构造函数.
     *
     * @param attrname 属性名称.
     * @param tpschema 属性类型所在schema.
     * @param typename 类型名称.
     * @param attflags 标记信息.
     * @param datatype 类型标识oid.
     * @param attypmod 属性的{@code attypmod}.
     * @param thevalue 元组的值.
     */
    private PgsqlComponentThe //
        /* */( String attrname //
        /* */, String tpschema //
        /* */, String typename //
        /* */, long datatype //
        /* */, long attypmod //
        /* */, long attflags //
        /* */, JsonNode thevalue //
        /* */)
    {
        super(attrname, tpschema, typename, datatype, attypmod, attflags);
        this.thevalue = thevalue;
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
        builder.append("|");
        appendJsonNodeTo(thevalue, builder);
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
        node.set("thevalue", thevalue);
    }
}
