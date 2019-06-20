/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.script;

import com.fasterxml.jackson.databind.JsonNode;
import com.hktcode.lang.exception.ArgumentNullException;

import javax.script.ScriptException;

public interface BasicScript
{
    String getEngineName();

    String getScriptText();

    <D> Object eval(D dollar) throws ScriptException;

    static BasicScript ofJsonObject(JsonNode json) throws ScriptException
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        if (json.isTextual()) {
            return GroovyScript.of(json.asText());
        }
        else {
            String engineName = json.get("engine_name").asText();
            String scriptText = json.get("script_text").asText();
            switch (engineName) {
                case GroovyScript.ENGINE_NAME:
                    return GroovyScript.of(scriptText);
                case NashornScript.ENGINE_NAME:
                    return NashornScript.of(scriptText);
                case AlwaysTrueScript.ENGINE_NAME:
                    return AlwaysTrueScript.of();
                case AlwaysFalseScript.ENGINE_NAME:
                    return AlwaysFalseScript.of();
                default:
                    throw new ScriptEngineNameUnknownException(engineName);
            }
        }
    }
}
