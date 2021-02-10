/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.hktcode.jackson.exception.JsonSchemaValidationImplException;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.exception.RuoshuiNameFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.regex.Pattern;

@RestController
@RequestMapping("api/recievers/upper")
public class UpperController
{
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-z][a-z_0-9]*(\\.[a-z][a-z_0-9]*)*$");

    private static final long NAME_MAXLENGTH = 128;

    private final UpperService service;

    private final JsonSchema upperConfigSchema;

    public UpperController(@Autowired UpperService service) throws ProcessingException
    {
        this.service = service;
        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        this.upperConfigSchema = factory.getJsonSchema(UpperConfig.SCHEMA);
    }

    @PutMapping("{name}")
    public ResponseEntity<UpperResult[]> put(@PathVariable("name") String name, @RequestBody JsonNode body) //
            throws InterruptedException, ProcessingException, IOException
    {
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        if (body == null) {
            throw new ArgumentNullException("body");
        }
        if (name.length() > NAME_MAXLENGTH) {
            throw new RuoshuiNameFormatException(name);
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new RuoshuiNameFormatException(name);
        }
        ProcessingReport report = upperConfigSchema.validate(body);
        if (!report.isSuccess()) {
            throw new JsonSchemaValidationImplException(report);
        }
        return this.service.put(name, (ObjectNode) body);
    }

    @DeleteMapping("{name}")
    public ResponseEntity<UpperResult[]> del(@PathVariable("name") String name) //
            throws InterruptedException
    {
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        if (name.length() > NAME_MAXLENGTH) {
            throw new RuoshuiNameFormatException(name);
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new RuoshuiNameFormatException(name);
        }
        return this.service.del(name);
    }

    @GetMapping("{name}")
    public ResponseEntity<UpperResult[]> get(@PathVariable("name") String name) //
            throws InterruptedException
    {
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        if (name.length() > NAME_MAXLENGTH) {
            throw new RuoshuiNameFormatException(name);
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new RuoshuiNameFormatException(name);
        }
        return this.service.get(name);
    }

    @GetMapping
    public ResponseEntity<UpperResult[]> get() throws InterruptedException
    {
        return this.service.get();
    }
}
