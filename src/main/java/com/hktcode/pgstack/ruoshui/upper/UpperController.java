/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.hktcode.jackson.exception.JsonSchemaValidationImplException;
import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("api/upper/ruoshui")
public class UpperController implements DisposableBean
{
    private static Logger logger = LoggerFactory.getLogger(UpperController.class);

    private final AtomicReference<UpperService> service = new AtomicReference<>(UpperServiceWaitingOnlyone.of());

    private final JsonSchema upperConfigSchema;

    public UpperController(@Autowired YAMLMapper mapper) //
        throws IOException, ProcessingException
    {
        Class<UpperController> clazz = UpperController.class;
        final String name = "/put-upper-schema.yaml";
        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        try(InputStream input = clazz.getResourceAsStream(name)) {
            JsonNode jsonNode = mapper.readTree(input);
            this.upperConfigSchema = factory.getJsonSchema(jsonNode);
        }
    }

    @PutMapping
    public ResponseEntity put(@RequestBody JsonNode body) //
        throws InterruptedException, ProcessingException
    {
        if (body == null) {
            throw new ArgumentNullException("body");
        }
        ProcessingReport report = upperConfigSchema.validate(body);
        if (!report.isSuccess()) {
            throw new JsonSchemaValidationImplException(report);
        }
        UpperConfig config = UpperConfig.of(body);
        UpperServiceWorking future;
        UpperService origin;
        do {
            origin = this.service.get();
            future = origin.putService();
        } while (future != origin && !this.service.compareAndSet(origin, future));
        return future.put(config);
    }

    @DeleteMapping
    public ResponseEntity del() throws InterruptedException
    {
        UpperServiceWaiting future;
        UpperService origin;
        do {
            origin = this.service.get();
            future = origin.delService();
        } while (origin != future && !this.service.compareAndSet(origin, future));
        return origin.del();
    }

    @GetMapping
    public ResponseEntity get() throws InterruptedException
    {
        UpperService service = this.service.get();
        return service.get();
    }

    @Override
    public void destroy() throws InterruptedException
    {
        this.del();
    }
}
