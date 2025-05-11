package com.dofinal.RG.exceptions;

import org.apache.skywalking.apm.toolkit.trace.Trace;

/**
 * &#064;Classname UpdateException
 * &#064;Description  TODO
 * &#064;Date 2024/5/18 22:05
 * &#064;Created MuJue
 */
public class UpdateException extends Exception {

    private final String message;

    @Override
    @Trace
    public String getMessage() {
        return message;
    }

    public UpdateException(String message) {
        super();
        this.message = message;
    }
}
