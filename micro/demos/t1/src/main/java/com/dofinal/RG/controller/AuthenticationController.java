package com.dofinal.RG.controller;

import com.dofinal.RG.entity.user.User;
import com.dofinal.RG.exceptions.AuthenticationException;
import com.dofinal.RG.reqs.entity.AuthenticationReq;
import com.dofinal.RG.rsps.BaseRsp;
import com.dofinal.RG.service.AuthenticationService;
import com.dofinal.RG.util.NoticeUtil;
import org.apache.ibatis.annotations.Param;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;

/**
 * &#064;Classname AuthenticationController
 * &#064;Description 这个类是注册和登录两个功能与前端的接口。
 * &#064;Date 2024/5/9 15:00
 * &#064;Created MuJue
 */
@CrossOrigin
@RestController
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private NoticeUtil noticeUtil;

    /**
     * &#064;description: 这个方法就对应登录操作。
     * &#064;author: MuJue
     * &#064;date: 2024/5/17 21:19
     * &#064;param: [req]
     * &#064;return: BaseRsp<User>
     **/
    @PostMapping("/login")
    @Trace
    public BaseRsp<User> login(@RequestBody AuthenticationReq req) {
        String originPassword = req.getPassword();
        // 进行密码加密。这个是为了安全性考虑。注意：数据库中存储的信息都是加密的。
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        // 因为是登录，所以我们需要返回一个基于User的Response，这个User的信息将长期存在于前端。
        BaseRsp<User> rsp = new BaseRsp<>();
        User user = null;
        try {
            user = authenticationService.login(req);
            user.setPassword(originPassword);
            rsp.setSuccess(true);
            rsp.setMessage("login success!");
            String content = "登陆成功！";
            noticeUtil.addNotice(req.getUid(), content);
        } catch (AuthenticationException e) {
            rsp.setMessage(e.getMessage());
            rsp.setSuccess(false);
        }
        rsp.setContent(user);
        return rsp;
    }

    /**
     * &#064;description: 这个方法就对应注册操作。
     * &#064;author: MuJue
     * &#064;date: 2024/5/17 21:21
     * &#064;param: [req]
     * &#064;return: BaseRsp(只用于表示操作是否成功)
     **/
    @PostMapping("/register")
    @Trace
    public BaseRsp register(@RequestBody AuthenticationReq req) {
        // 同样需要进行密码加密操作。
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        // 注意:我们的注册业务逻辑时，用户注册完成后，还需要返回登录页面进行登录，所以这里的Response只是告诉前端注册操作是否成功。
        BaseRsp rsp = new BaseRsp();
        boolean res = false;
        try {
            res = authenticationService.register(req);
            if (res) {
                rsp.setMessage("register success! Now you can login!");
                rsp.setSuccess(true);
            }
        } catch (AuthenticationException e) {
            rsp.setSuccess(false);
            rsp.setMessage(e.getMessage());
        }
        return rsp;
    }

    @PostMapping("/logout")
    @Trace
    public BaseRsp logout(@RequestBody AuthenticationReq req) {
        authenticationService.logout(req);
        BaseRsp rsp = new BaseRsp();
        rsp.setMessage("logout success!");
        rsp.setSuccess(true);
        return rsp;
    }
}
