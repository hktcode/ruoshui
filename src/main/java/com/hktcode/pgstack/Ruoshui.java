/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack;

import com.hktcode.lang.Application;
import com.hktcode.pgstack.ruoshui.RuoshuiConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

/**
 * 应用程序类.
 */
public class Ruoshui
{
    /**
     * 应用程序HOME目录位置.
     */
    public static final String HOME = Application.getHome("RUOSHUI_HOME");

    public static final String THE_NAME = "ruoshui";

    private static final Logger logger = LoggerFactory.getLogger(Ruoshui.class);

    public static void main(String[] args)
    {
        logger.info("ruoshui starts: HOME={}", HOME);
        if (args == null) {
            logger.warn("args is null, I will use new String[0] instead.");
            args = new String[0];
        }
        for (int i = 0; i != args.length; ++i) {
            logger.info("    args[{}]={}", i, args[i]);
        }

        SpringApplication.run(RuoshuiConfiguration.class, args);
    }
}
