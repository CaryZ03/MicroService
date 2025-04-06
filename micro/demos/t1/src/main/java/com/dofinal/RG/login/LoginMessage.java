package com.dofinal.RG.login;


/**
 * &#064;Classname LoginMessage
 * &#064;Description  the class would stock information of uid and password deliver from web.
 * &#064;Date 2024/4/21 14:30
 * &#064;Created MuJue
 */
public class LoginMessage {
    private final String uid;
    private final String password;
    private final String type; //登录还是注册
    public LoginMessage(String uid, String password,String type) {
        this.uid = uid;
        this.password = password;
        this.type = type;
    }
}
