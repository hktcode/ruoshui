/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.hktcode.lang.exception.ArgumentNullException;
import org.springframework.http.ResponseEntity;

public interface WaitingService extends UpperService
{
    @Override
    default ResponseEntity del()
    {
        return ResponseEntity.notFound().build();
    }

    @Override
    default ResponseEntity get()
    {
        return ResponseEntity.notFound().build();
    }

    @Override
    default ResponseEntity pst(JsonNode json)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    default WaitingService delService()
    {
        return this;
    }
}
