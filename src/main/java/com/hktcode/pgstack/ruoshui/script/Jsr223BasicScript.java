/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.script;

import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;

import javax.script.*;
import java.io.PrintWriter;

public abstract class Jsr223BasicScript implements BasicScript
{
    protected final String scriptText;

    protected final CompiledScript scriptEval;

    protected Jsr223BasicScript(String scriptText, CompiledScript scriptEval)
    {
        this.scriptText = scriptText;
        this.scriptEval = scriptEval;
    }

    @Override
    public String getScriptText()
    {
        return this.scriptText;
    }

    protected <D> Object eval(ScriptEngine engine, D dollar, Logger logger)
        throws ScriptException
    {
        if (engine == null) {
            throw new ArgumentNullException("engine");
        }
        if (dollar == null) {
            throw new ArgumentNullException("dollar");
        }
        Bindings bindings = engine.createBindings();
        bindings.put("$", dollar);
        bindings.put("logger", logger);
        ScriptContext scriptContext = engine.getContext();
        scriptContext.setWriter(new PrintWriter(System.out));
        scriptContext.setErrorWriter(new PrintWriter(System.err));
        scriptContext.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
        return this.scriptEval.eval(scriptContext);
    }
}
