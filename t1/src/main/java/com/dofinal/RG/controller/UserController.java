package com.dofinal.RG.controller;

import com.dofinal.RG.entity.order.UserOrder;
import com.dofinal.RG.entity.order.UserOrderDemo;
import com.dofinal.RG.entity.user.Customer;
import com.dofinal.RG.entity.user.User;
import com.dofinal.RG.reqs.entity.CustomerReq;
import com.dofinal.RG.reqs.entity.UserReq;
import com.dofinal.RG.rsps.BaseRsp;
import com.dofinal.RG.service.OrderService;
import com.dofinal.RG.service.UserService;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * &#064;Classname UserController
 * &#064;Description 这个类是和用户本身相关的操作的前端接口
 * &#064;Date 2024/5/4 20:24
 * &#064;Created MuJue
 */
@CrossOrigin
@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;

    /**
     * &#064;description: 通过主键(uid)来查询用户
     * &#064;author: MuJue
     * &#064;date: 2024/5/17 21:36
     * &#064;param: [uid]
     * &#064;return: BaseRsp<User>
     **/
    @GetMapping("/user/getByUid/{uid}")
    @Trace
    public BaseRsp<User> getUserByUid(@PathVariable("uid") String uid) {
        User user = userService.findUserByUid(uid);
        BaseRsp<User> userBaseRsp = new BaseRsp<>();
        if (user != null) {
            userBaseRsp.setSuccess(true);
            userBaseRsp.setMessage("get user by id success!");
        } else {
            userBaseRsp.setSuccess(false);
            userBaseRsp.setMessage("get user by id fail!");
        }
        userBaseRsp.setContent(user);
        return userBaseRsp;
    }

    @GetMapping("/user/getUserCustomer/{uid}")
    @Trace
    public BaseRsp<List<Customer>> getUserCustomerByUid(@PathVariable("uid") String uid) {
        List<Customer> customers = userService.getCustomersByUid(uid);
        BaseRsp<List<Customer>> rsp = new BaseRsp<>();
        if (customers != null) {
            rsp.setSuccess(true);
            rsp.setMessage("get customers by uid success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("get customers by uid fail!");
        }
        rsp.setContent(customers);
        return rsp;
    }

    @PostMapping("/user/getUserOrder/{uid}")
    @Trace
    public BaseRsp<List<UserOrderDemo>> getUserOrderByUid(@PathVariable("uid") String uid) {
        List<UserOrderDemo> userOrders = orderService.getUserOrdersByUid(uid);
        BaseRsp<List<UserOrderDemo>> rsp = new BaseRsp<>();
        if (userOrders != null) {
            rsp.setSuccess(true);
            rsp.setMessage("get userOrders by uid success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("get userOrders by uid fail!");
        }
        rsp.setContent(userOrders);
        return rsp;
    }

    /**
     * &#064;description: 通过uid来查询一个用户及其所有的信息（顾客，订单）
     * &#064;author: MuJue
     * &#064;date: 2024/5/17 21:38
     * &#064;param: [uid]
     * &#064;return: BaseRsp<User>
     **/
    @GetMapping("/user/getAllInfo/{uid}")
    @Trace
    public BaseRsp<User> getUserWithOrderAndCustomerByUid(@PathVariable("uid") String uid) {
        User user = userService.findUserWithOrderAndCustomerByUid(uid);
        BaseRsp<User> userBaseRsp = new BaseRsp<>();
        if (user != null) {
            userBaseRsp.setSuccess(true);
            userBaseRsp.setMessage("get user all info by id success!");
        } else {
            userBaseRsp.setSuccess(false);
            userBaseRsp.setMessage("get user all info by id fail!");
        }
        userBaseRsp.setContent(user);
        return userBaseRsp;
    }

    /**
     * &#064;description: 获得所有用户的信息
     * &#064;author: MuJue
     * &#064;date: 2024/5/17 21:39
     * &#064;param: []
     * &#064;return: BaseRsp<List<User>>
     **/
    @GetMapping("/user/getAll")
    @Trace
    public BaseRsp<List<User>> getUsers() {
        List<User> userList = userService.findUsers();
        BaseRsp<List<User>> userListBaseRsp = new BaseRsp<>();
        if (userList != null) {
            userListBaseRsp.setSuccess(true);
            userListBaseRsp.setMessage("get all users success!");
        } else {
            userListBaseRsp.setSuccess(false);
            userListBaseRsp.setMessage("get all users fail!");
        }
        userListBaseRsp.setContent(userList);
        return userListBaseRsp;
    }

    /**
     * &#064;description: 通过uid删除用户信息
     * &#064;author: MuJue
     * &#064;date: 2024/5/17 21:41
     * &#064;param: [uid, redirectAttributes]
     * &#064;return: BaseRsp
     **/
    @DeleteMapping("/user/deleteByUid/{uid}")
    @Trace
    public BaseRsp deleteUserById(@PathVariable("uid") String uid) {
        int res = userService.deleteUserByUid(uid);
        BaseRsp rsp = new BaseRsp();
        if (res != 0) {
            rsp.setSuccess(true);
            rsp.setMessage("delete user by uid success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("delete user by uid fail!");
        }
        // redirectAttributes.addFlashAttribute("/");
        return rsp;
    }

    /**
     * &#064;description: 只更新用户的个人信息。注意这点。
     * &#064;author: MuJue
     * &#064;date: 2024/5/17 21:42
     * &#064;param: [UserReq]
     * &#064;return: com.dofinal.RG.rsps.BaseRsp<com.dofinal.RG.entity.user.User>
     **/
    @PutMapping("/user/update/")
    @Trace
    public BaseRsp<User> updateUser(@RequestBody UserReq UserReq) {
        String originPassword = UserReq.getPassword();
        UserReq.setPassword(DigestUtils.md5DigestAsHex(UserReq.getPassword().getBytes()));
        int res = userService.updateUser(UserReq);
        BaseRsp<User> rsp = new BaseRsp<>();
        if (res != 0) {
            rsp.setSuccess(true);
            rsp.setMessage("update user success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("update user fail!");
        }
        User user = userService.findUserByUid(UserReq.getUid());
        user.setPassword(originPassword);
        rsp.setContent(user);
        return rsp;
    }

    @PostMapping("/user/addCustomer")
    @Trace
    public BaseRsp<Boolean> addUserCustomer(@RequestBody CustomerReq req) {
        int res = userService.addUserCustomer(req);
        BaseRsp<Boolean> rsp = new BaseRsp<>();
        if (res != 0) {
            rsp.setSuccess(true);
            rsp.setMessage("add user customer success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("add user customer fail!");
        }
        rsp.setContent(res != 0);
        return rsp;
    }

    @PostMapping("/user/deleteCustomer")
    @Trace
    public BaseRsp<Boolean> cancelUserCustomer(@RequestBody CustomerReq req) {
        int res = userService.deleteUserCustomer(req);
        BaseRsp<Boolean> rsp = new BaseRsp<>();
        if (res != 0) {
            rsp.setSuccess(true);
            rsp.setMessage("cancel user customer success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("cancel user customer fail!");
        }
        rsp.setContent(res != 0);
        return rsp;
    }
}
