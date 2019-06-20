/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.script;

public class AlwaysTrueScript implements BasicScript
{
    public static final String ENGINE_NAME = "always-true";

    public static AlwaysTrueScript of()
    {
        return new AlwaysTrueScript();
    }

    private AlwaysTrueScript()
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
        return "true";
    }

    @Override
    public <D> Object eval(D dollar)
    {
        return true;
    }
}
