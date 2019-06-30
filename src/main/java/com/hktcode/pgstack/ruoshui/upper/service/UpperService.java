/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

import javax.script.ScriptException;
import java.util.concurrent.ExecutionException;

public interface UpperService
{
    ResponseEntity del() throws ExecutionException, InterruptedException;

    ResponseEntity get() throws ExecutionException, InterruptedException;

    ResponseEntity pst(JsonNode json) throws ExecutionException, InterruptedException, ScriptException;

    WorkingService putService();

    WaitingService delService();
}