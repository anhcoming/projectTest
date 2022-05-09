package com.viettel.hstd.service.imp;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggerServiceImp {
    protected Logger logger = LoggerFactory.getLogger("console");

    /*INFO*/
    public void info(String name) {
        logger.info(name);
    }

    public void info(String name, Object object) {
        logger.info(name + " " + new Gson().toJson(object));
    }

    /*ERROR*/
    public void error(String name) {
        logger.error(name);
    }

    public void error(String name, Object object) {
        logger.error(name + " " + new Gson().toJson(object));
    }

    /*DEBUG*/
    public void debug(String name) {
        logger.debug(name);
    }

    public void debug(String name, Object object) {
        logger.debug(name + " " + new Gson().toJson(object));
    }
    /*WARN*/
    public void warn(String name) {
        logger.warn(name);
    }

    public void warn(String name, Object object) {
        logger.warn(name + " " + new Gson().toJson(object));
    }
}
