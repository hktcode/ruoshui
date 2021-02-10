/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

public interface UpperService
{
    ResponseEntity put(String name, UpperConfig config) throws InterruptedException;

    ResponseEntity del(String name) throws InterruptedException;

    ResponseEntity get(String name) throws InterruptedException;

    ResponseEntity get() throws InterruptedException;

    ResponseEntity pst(String name, JsonNode json) throws InterruptedException;

    void destroy() throws InterruptedException;
}
