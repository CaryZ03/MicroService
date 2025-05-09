package com.dofinal.RG.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dofinal.RG.entity.order.UserOrder;
import com.dofinal.RG.entity.order.UserOrderDemo;
import com.dofinal.RG.entity.user.Customer;
import com.dofinal.RG.entity.user.User;
import com.dofinal.RG.reqs.entity.CustomerReq;
import com.dofinal.RG.reqs.entity.UserReq;

import java.util.List;

/**
 * &#064;Classname UserService
 * &#064;Description  TODO
 * &#064;Date 2024/5/5 20:47
 * &#064;Created MuJue
 */
public interface UserService extends IService<User> {
    List<User> findUsers();

    User findUserByUid(String uid);

    User findUserWithOrderAndCustomerByUid(String uid);

    int deleteUserByUid(String uid);

    int updateUser(UserReq UserReq);

    int addUserCustomer(CustomerReq customerReq);

    int deleteUserCustomer(CustomerReq customerReq);

    List<Customer> getCustomersByUid(String uid);
    int updateUser(User user);
}
