package com.dofinal.RG.exceptions;

/**
 * &#064;Classname SelectException
 * &#064;Description  TODO
 * &#064;Date 2024/5/18 22:05
 * &#064;Created MuJue
 */
public class SelectException extends Exception{
    private final String message;

    @Override
    public String getMessage() {
        return message;
    }
    public SelectException(String message) {
        super();
        this.message = message;
    }
}
