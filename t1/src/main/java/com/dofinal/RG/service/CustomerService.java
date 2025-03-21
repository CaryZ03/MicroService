package com.dofinal.RG.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dofinal.RG.entity.user.Customer;
import com.dofinal.RG.reqs.entity.CustomerReq;
import org.apache.skywalking.apm.toolkit.trace.Trace;

import java.util.List;

/**
 * &#064;Classname CustomerService
 * &#064;Description  TODO
 * &#064;Date 2024/5/16 21:46
 * &#064;Created MuJue
 */
public interface CustomerService extends IService<Customer> {
    @Trace
    Customer findCustomerById(int cid);
    @Trace
    List<Customer> findCustomerByUid(String uid);
    @Trace
    List<Customer> findCustomers();
    @Trace
    List<Integer> getCidByTid(String tid);
    @Trace
    List<Integer> getCidByTidStrict(String tid);
    @Trace
    List<Integer> getCidByHid(int hid);
    @Trace
    int addCustomer(CustomerReq customerReq);
    @Trace
    int updateCustomer(CustomerReq customerReq);
    @Trace
    int deleteCustomerByCid(int cid);
}
