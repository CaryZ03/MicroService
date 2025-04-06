package com.dofinal.RG.rsps;

/**
 * &#064;Classname BaseRsp
 * &#064;Description  TODO
 * &#064;Date 2024/5/10 21:55
 * &#064;Created MuJue
 */
public class BaseRsp<T>{
    private boolean success = true;
    private String message;
    private T content;

    @Override
    public String toString() {
        return "BaseRsp{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", content=" + content +
                '}';
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getContent() {
        return content;
    }
}
