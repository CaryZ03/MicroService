package com.dofinal.RG.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dofinal.RG.entity.user.User;
import com.dofinal.RG.exceptions.AuthenticationException;
import com.dofinal.RG.reqs.entity.AuthenticationReq;

/**
 * &#064;Classname AuthenticationService
 * &#064;Description TODO
 * &#064;Date 2024/5/9 15:01
 * &#064;Created MuJue
 */
public interface AuthenticationService extends IService<User> {

    boolean register(AuthenticationReq req) throws AuthenticationException;

    User login(AuthenticationReq req) throws AuthenticationException;

    void logout(AuthenticationReq req);
}
