package com.dofinal.RG.exceptions;

import org.apache.skywalking.apm.toolkit.trace.Trace;

/**
 * &#064;Classname InsertException
 * &#064;Description  TODO
 * &#064;Date 2024/5/18 22:06
 * &#064;Created MuJue
 */
public class InsertException extends Exception {

    private final String message;

    @Override
    @Trace
    public String getMessage() {
        return message;
    }

    public InsertException(String message) {
        super();
        this.message = message;
    }
}
