/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper;

import org.springframework.http.ResponseEntity;

public interface UpperServiceWorking extends UpperService
{
    ResponseEntity put(UpperConfig config) throws InterruptedException;
}
