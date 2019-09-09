/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

import javax.script.ScriptException;

public interface UpperService
{
    ResponseEntity del() throws InterruptedException;

    ResponseEntity get() throws InterruptedException;

    ResponseEntity pst(JsonNode json) throws InterruptedException, ScriptException;

    UpperServiceWorking putService();

    UpperServiceWaiting delService();
}
