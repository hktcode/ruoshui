/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.script;

import com.hktcode.lang.exception.ArgumentNullException;
import org.codehaus.groovy.jsr223.GroovyScriptEngineImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.PrintWriter;

public class GroovyScript extends Jsr223BasicScript
{
    public static GroovyScript of(String scriptText) throws ScriptException
    {
        if (scriptText == null) {
            throw new ArgumentNullException("scriptText");
        }
        scriptText = "" //
            + "import java.time.*;\n" //
            + "\n" //
            + scriptText;
        return new GroovyScript(scriptText);
    }

    public static final Logger logger = LoggerFactory.getLogger(NashornScript.class);

    public static final String ENGINE_NAME = "groovy";

    public static final GroovyScriptEngineImpl ENGINE = (GroovyScriptEngineImpl) (new ScriptEngineManager().getEngineByName(ENGINE_NAME));

    private GroovyScript(String scriptText) throws ScriptException
    {
        super(scriptText, ENGINE.compile(scriptText));
    }

    @Override
    public String getEngineName()
    {
        return ENGINE_NAME;
    }

    @Override
    public <D> Object eval(D dollar) throws ScriptException
    {
        if (dollar == null) {
            throw new ArgumentNullException("dollar");
        }
        Bindings bindings = ENGINE.createBindings();
        bindings.put("dollar", dollar);
        bindings.put("logger", logger);
        ScriptContext scriptContext = ENGINE.getContext();
        scriptContext.setWriter(new PrintWriter(System.out));
        scriptContext.setErrorWriter(new PrintWriter(System.err));
        scriptContext.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
        return super.scriptEval.eval(scriptContext);
    }
}
