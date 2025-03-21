package com.dofinal.RG.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dofinal.RG.entity.order.UserOrder;
import com.dofinal.RG.entity.order.UserOrderDemo;
import com.dofinal.RG.entity.user.Customer;
import com.dofinal.RG.entity.user.User;
import com.dofinal.RG.reqs.entity.CustomerReq;
import com.dofinal.RG.reqs.entity.UserReq;
import org.apache.skywalking.apm.toolkit.trace.Trace;

import java.util.List;

/**
 * &#064;Classname UserService
 * &#064;Description  TODO
 * &#064;Date 2024/5/5 20:47
 * &#064;Created MuJue
 */
public interface UserService extends IService<User> {
    @Trace
    List<User> findUsers();

    @Trace
    User findUserByUid(String uid);

    @Trace
    User findUserWithOrderAndCustomerByUid(String uid);

    @Trace
    int deleteUserByUid(String uid);

    @Trace
    int updateUser(UserReq UserReq);

    @Trace
    int addUserCustomer(CustomerReq customerReq);

    @Trace
    int deleteUserCustomer(CustomerReq customerReq);

    @Trace
    List<Customer> getCustomersByUid(String uid);
}
