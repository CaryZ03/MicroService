package com.dofinal.RG.exceptions;

/**
 * &#064;Classname AuthenticationException
 * &#064;Description  TODO
 * &#064;Date 2024/5/18 22:04
 * &#064;Created MuJue
 */
public class AuthenticationException extends Exception{
    private final String message;

    @Override
    public String getMessage() {
        return message;
    }
    public AuthenticationException(String message) {
        super();
        this.message = message;
    }
}
