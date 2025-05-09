package com.dofinal.RG.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dofinal.RG.entity.user.Customer;
import com.dofinal.RG.reqs.entity.CustomerReq;

import java.util.List;

/**
 * &#064;Classname CustomerService
 * &#064;Description  TODO
 * &#064;Date 2024/5/16 21:46
 * &#064;Created MuJue
 */
public interface CustomerService extends IService<Customer> {
    Customer findCustomerById(int cid);
    List<Customer> findCustomerByUid(String uid);
    List<Customer> findCustomers();
    List<Integer> getCidByTid(String tid);
    List<Integer> getCidByTidStrict(String tid);
    List<Integer> getCidByHid(int hid);
    int addCustomer(CustomerReq customerReq);
    int updateCustomer(CustomerReq customerReq);
    int deleteCustomerByCid(int cid);
    Customer findCustomerByCid(int cid);
}
