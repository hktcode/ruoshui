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

@RestController
@RequestMapping("api/upper/")
public class UpperController implements DisposableBean
{
    private static final Logger logger = LoggerFactory.getLogger(UpperController.class);

    private final UpperService service = UpperServiceOnlyone.of();

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

    @PutMapping("{name}")
    public ResponseEntity put(@PathVariable("name") String name, @RequestBody JsonNode body) //
        throws InterruptedException, ProcessingException
    {
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        if (body == null) {
            throw new ArgumentNullException("body");
        }
        ProcessingReport report = upperConfigSchema.validate(body);
        if (!report.isSuccess()) {
            throw new JsonSchemaValidationImplException(report);
        }
        UpperConfig config = UpperConfig.of(body);
        return this.service.put(name, config);
    }

    @DeleteMapping("{name}")
    public ResponseEntity del(@PathVariable("name") String name) //
            throws InterruptedException
    {
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        return this.service.del(name);
    }

    @GetMapping("{name}")
    public ResponseEntity get(@PathVariable("name") String name) throws InterruptedException
    {
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        return this.service.get(name);
    }

    @GetMapping
    public ResponseEntity get() throws InterruptedException
    {
        return this.service.get();
    }

    @Override
    public void destroy() throws InterruptedException
    {
        this.service.destroy();
    }
}
