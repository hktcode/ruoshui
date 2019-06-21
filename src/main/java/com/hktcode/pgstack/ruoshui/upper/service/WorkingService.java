/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.service;

import com.hktcode.pgstack.ruoshui.upper.entity.UpperConfig;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.ExecutionException;

public interface WorkingService extends UpperService
{
    ResponseEntity put(UpperConfig config) //
        throws ExecutionException, InterruptedException;
}
