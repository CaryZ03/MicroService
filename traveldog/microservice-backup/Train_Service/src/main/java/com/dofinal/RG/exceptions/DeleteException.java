package com.dofinal.RG.exceptions;

/**
 * &#064;Classname DeleteException
 * &#064;Description  TODO
 * &#064;Date 2024/5/18 22:05
 * &#064;Created MuJue
 */
public class DeleteException extends Exception{
    private final String message;

    @Override
    public String getMessage() {
        return message;
    }
    public DeleteException(String message) {
        super();
        this.message = message;
    }
}
