package com.dofinal.RG.exceptions;

/**
 * &#064;Classname PurchaseException
 * &#064;Description  TODO
 * &#064;Date 2024/5/18 22:06
 * &#064;Created MuJue
 */
public class PurchaseException extends Exception{
    private final String message;

    @Override
    public String getMessage() {
        return message;
    }
    public PurchaseException(String message) {
        super();
        this.message = message;
    }
}
