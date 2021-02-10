/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui;

import com.hktcode.bgsimple.SimpleStatusIsNotPutException;
import com.hktcode.jackson.JacksonExceptionHandler;
import com.hktcode.lang.exception.ArgumentNullException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class RuoshuiExceptionHandler extends JacksonExceptionHandler
{
    @ExceptionHandler
    public ResponseEntity simpleStatusIsNotPutExceptionHandler(SimpleStatusIsNotPutException ex, HttpServletRequest req)
    {
        if (ex == null) {
            throw new ArgumentNullException("ex");
        }
        if (req == null) {
            throw new ArgumentNullException("req");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
