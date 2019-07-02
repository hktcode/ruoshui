/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.script;

public class AlwaysFalseScript implements BasicScript
{
    public static final String ENGINE_NAME = "always-false";

    public static AlwaysFalseScript of()
    {
        return new AlwaysFalseScript();
    }

    private AlwaysFalseScript()
    {
    }

    @Override
    public String getEngineName()
    {
        return ENGINE_NAME;
    }

    @Override
    public String getScriptText()
    {
        return "false";
    }

    @Override
    public <D> Object eval(D dollar)
    {
        return false;
    }
}
