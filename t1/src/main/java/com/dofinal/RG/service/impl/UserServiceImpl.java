package com.dofinal.RG.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dofinal.RG.entity.location.Location;
import com.dofinal.RG.entity.order.*;
import com.dofinal.RG.entity.user.Customer;
import com.dofinal.RG.entity.user.User;
import com.dofinal.RG.entity.user.UserCustomer;
import com.dofinal.RG.mapper.*;
import com.dofinal.RG.reqs.entity.CustomerReq;
import com.dofinal.RG.reqs.entity.UserReq;
import com.dofinal.RG.service.OrderService;
import com.dofinal.RG.service.UserService;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * &#064;Classname UserServiceImpl
 * &#064;Description TODO
 * &#064;Date 2024/5/5 20:50
 * &#064;Created MuJue
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private UserOrderMapper userOrderMapper;
    @Autowired
    private HotelOrderMapper hotelOrderMapper;
    @Autowired
    private TrainOrderMapper trainOrderMapper;
    @Autowired
    private MealOrderMapper mealOrderMapper;
    @Autowired
    private UserCustomerMapper userCustomerMapper;
    @Autowired
    private OrderService orderService;

    @Override
    @Trace
    public List<User> findUsers() {
        return userMapper.findUsers();
    }

    @Override
    @Trace
    public User findUserByUid(String uid) {
        if (uid != null) {
            return userMapper.findUserByUid(uid);
        }
        return null;
    }

    @Override
    @Trace
    public List<Customer> getCustomersByUid(String uid) {
        if (uid == null)
            return null;
        List<Customer> customerList = new ArrayList<>();
        List<Integer> cids = userCustomerMapper.getCidByUid(uid);
        for (Integer cid : cids) {
            Customer customer = customerMapper.findCustomerByCid(cid);
            customerList.add(customer);
        }
        return customerList;
    }

    @Override
    @Trace
    public User findUserWithOrderAndCustomerByUid(String uid) {
        if (uid != null) {
            User user = userMapper.findUserByUid(uid);
            List<Customer> customerList = getCustomersByUid(uid);
            List<UserOrderDemo> userOrderDemoList = orderService.getUserOrdersByUid(uid);
            user.setCustomers(customerList);
            user.setUserOrders(userOrderDemoList);
            return user;
        }
        return null;
    }

    @Override
    @Trace
    public int deleteUserByUid(String uid) {
        if (uid != null) {
            List<Integer> oids = userOrderMapper.getOidByUid(uid);
            for (Integer oid : oids) {
                hotelOrderMapper.deleteHotelOderByOid(oid);
                mealOrderMapper.deleteMealOrderByOid(oid);
                trainOrderMapper.deleteTrainOrderByOid(oid);
            }
            userOrderMapper.deleteUserOrderByUid(uid);
            userCustomerMapper.deleteUserCustomerByUid(uid);
            userMapper.deleteUserByUid(uid);
            return 1;
        }
        return 0;
    }

    @Override
    @Trace
    public int updateUser(UserReq UserReq) {
        if (UserReq == null)
            return -1;
        User user = UserReq.getUser();
        return userMapper.updateUser(user);
    }

    @Override
    @Trace
    public int addUserCustomer(CustomerReq customerReq) {
        if (customerReq == null)
            return -1;
        String uid = customerReq.getUid();
        Customer customer = customerMapper.findCustomerByIdCard(customerReq.getIdCard());
        if (customer != null) {
            if (!customer.getName().equals(customerReq.getcName())) {
                return 0;
            }
            int cid = customerMapper.findCustomerByIdCard(customerReq.getIdCard()).getId();
            UserCustomer us = userCustomerMapper.getUserCustomerByUidCid(uid, cid);
            if (us != null) {
                return 1;
            }
            us = new UserCustomer(uid, cid);
            return userCustomerMapper.insertUserCustomer(us);
        }
        customer = customerReq.getCustomer();
        int res = customerMapper.insertCustomer(customer);
        if (res == 0) {
            return 0;
        }
        int cid = customerMapper.findCustomerByIdCard(customerReq.getIdCard()).getId();
        UserCustomer us = new UserCustomer(uid, cid);
        return userCustomerMapper.insertUserCustomer(us);
    }

    @Override
    @Trace
    public int deleteUserCustomer(CustomerReq customerReq) {
        if (customerReq == null)
            return -1;
        String uid = customerReq.getUid();
        int cid = customerReq.getCid();
        return userCustomerMapper.deleteUserCustomerByUidCid(uid, cid);
    }
}
