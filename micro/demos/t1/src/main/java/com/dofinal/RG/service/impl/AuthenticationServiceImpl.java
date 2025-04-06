package com.dofinal.RG.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dofinal.RG.entity.user.User;
import com.dofinal.RG.exceptions.AuthenticationException;
import com.dofinal.RG.mapper.UserMapper;
import com.dofinal.RG.reqs.entity.AuthenticationReq;
import com.dofinal.RG.service.AuthenticationService;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * &#064;Classname AuthenticationServiceImpl
 * &#064;Description TODO
 * &#064;Date 2024/5/9 15:01
 * &#064;Created MuJue
 */
@Service
public class AuthenticationServiceImpl extends ServiceImpl<UserMapper, User> implements AuthenticationService {
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Trace
    public boolean register(AuthenticationReq req) throws AuthenticationException {
        // 注册，需要保证没有已存在的账号，所以我们要看看，数据库中是否记录了一个uid相同的User。
        String uid = req.getUid();
        String password = req.getPassword();
        User user = userMapper.findUserByUid(uid);
        // 账号已有！所以返回false
        if (user != null) {
            throw new AuthenticationException("用户已注册");
        }
        // 由于是新注册的用户，电话号码和名字都是没有设置的，所以以null进行初始化。
        user = new User(uid, null, password, null, 2000.0, "离线");
        // 既然要注册，肯定要把相应的信息插入到数据库里面。
        userMapper.insertUser(user);
        return true;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Trace
    public User login(AuthenticationReq req) throws AuthenticationException {
        // 登录，需要保证有一个存在的账号，所以我们从数据库中查询出以uid为账号的User.
        String uid = req.getUid();
        String password = req.getPassword();
        User user = userMapper.findUserByUid(uid);
        // 如果有这样的用户，而且密码是相同的，那么登录成功！否则失败。
        if (user != null) {
            if (user.getStatus().equals("在线")) {
                throw new AuthenticationException("用户已在线");
            }
            String password2 = user.getPassword();
            // System.out.println("p1: " + password + " p2: " + password2);
            if (password2.equals(password)) {
                user.setStatus("在线");
                userMapper.updateUser(user);
                return user;
            } else {
                throw new AuthenticationException("密码错误");
            }
        }
        throw new AuthenticationException("不存在该用户");
    }

    @Override
    @Trace
    public void logout(AuthenticationReq req) {
        User user = userMapper.findUserByUid(req.getUid());
        user.setStatus("离线");
        userMapper.updateUser(user);
    }
}
