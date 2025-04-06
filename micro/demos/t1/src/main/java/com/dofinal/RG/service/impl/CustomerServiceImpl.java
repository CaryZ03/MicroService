package com.dofinal.RG.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dofinal.RG.entity.order.HotelCustomerOrder;
import com.dofinal.RG.entity.order.TrainCustomerOrder;
import com.dofinal.RG.entity.order.UserOrder;
import com.dofinal.RG.entity.user.Customer;
import com.dofinal.RG.mapper.*;
import com.dofinal.RG.reqs.entity.CustomerReq;
import com.dofinal.RG.service.CustomerService;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * &#064;Classname CustomerServiceImpl
 * &#064;Description  TODO
 * &#064;Date 2024/5/16 21:48
 * &#064;Created MuJue
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {
    @Autowired
    CustomerMapper customerMapper;
    @Autowired
    UserCustomerMapper userCustomerMapper;
    @Autowired
    TrainOrderMapper trainOrderMapper;
    @Autowired
    HotelOrderMapper hotelOrderMapper;
    @Autowired
    UserOrderMapper userOrderMapper;

    @Override
    @Trace
    public Customer findCustomerById(int cid) {
        if (cid >= 1) {
            return customerMapper.findCustomerByCid(cid);
        }
        return null;
    }

    @Override
    @Trace
    public List<Customer> findCustomerByUid(String uid) {
        if (uid != null) {
            List<Integer> cids = userCustomerMapper.getCidByUid(uid);
            List<Customer> customers = new ArrayList<>();
            for (Integer cid : cids) {
                Customer customer = customerMapper.findCustomerByCid(cid);
                customers.add(customer);
            }
            return customers;
        }
        return null;
    }

    @Override
    @Trace
    public List<Customer> findCustomers() {
        return customerMapper.findCustomers();
    }

    @Override
    @Trace
    public List<Integer> getCidByTid(String tid) {
        List<TrainCustomerOrder> trainCustomerOrders = trainOrderMapper.findTrainOrderByTid(tid);
        Set<Integer> oids = new HashSet<>();
        Set<Integer> validOids = new HashSet<>();
        for(TrainCustomerOrder trainCustomerOrder: trainCustomerOrders){
            oids.add(trainCustomerOrder.getOrderId());
        }
        for(Integer oid : oids){
            UserOrder userOrder = userOrderMapper.getUserOrderByOid(oid);
            String status = userOrder.getStatus();
            if(status.equals("not paid") || status.equals("paid")){
                validOids.add(oid);
            }
        }
        List<Integer> cids = new ArrayList<>();
        for(Integer oid : validOids){
            trainCustomerOrders = trainOrderMapper.findTrainOrderByOid(oid);
            for(TrainCustomerOrder trainCustomerOrder : trainCustomerOrders){
                cids.add(trainCustomerOrder.getCustomerId());
            }
        }
        return cids;
    }

    @Override
    @Trace
    public List<Integer> getCidByTidStrict(String tid) {
        List<TrainCustomerOrder> trainCustomerOrders = trainOrderMapper.findTrainOrderByTid(tid);
        Set<Integer> oids = new HashSet<>();
        Set<Integer> validOids = new HashSet<>();
        for(TrainCustomerOrder trainCustomerOrder: trainCustomerOrders){
            oids.add(trainCustomerOrder.getOrderId());
        }
        for(Integer oid : oids){
            UserOrder userOrder = userOrderMapper.getUserOrderByOid(oid);
            String status = userOrder.getStatus();
            if(status.equals("paid")){
                validOids.add(oid);
            }
        }
        List<Integer> cids = new ArrayList<>();
        for(Integer oid : validOids){
            trainCustomerOrders = trainOrderMapper.findTrainOrderByOid(oid);
            for(TrainCustomerOrder trainCustomerOrder : trainCustomerOrders){
                cids.add(trainCustomerOrder.getCustomerId());
            }
        }
        return cids;
    }

    @Override
    @Trace
    public List<Integer> getCidByHid(int hid) {
        List<HotelCustomerOrder> hotelCustomerOrders = hotelOrderMapper.findHotelOrderByHid(hid);
        Set<Integer> oids = new HashSet<>();
        Set<Integer> validOids = new HashSet<>();
        for(HotelCustomerOrder hotelCustomerOrder : hotelCustomerOrders){
            oids.add(hotelCustomerOrder.getOrderId());
        }
        for(Integer oid : oids){
            UserOrder userOrder = userOrderMapper.getUserOrderByOid(oid);
            String status = userOrder.getStatus();
            if(status.equals("not paid") || status.equals("paid")){
                validOids.add(oid);
            }
        }
        List<Integer> cids = new ArrayList<>();
        for(Integer oid : validOids){
            hotelCustomerOrders = hotelOrderMapper.findHotelOrderByOid(oid);
            for(HotelCustomerOrder hotelCustomerOrder : hotelCustomerOrders){
                cids.add(hotelCustomerOrder.getCustomerId());
            }
        }
        return cids;
    }


    @Override
    @Trace
    public int updateCustomer(CustomerReq customerReq) {
        if (customerReq == null) return -1;

        Customer customer = customerReq.getCustomer();
        return customerMapper.updateCustomer(customer);
    }

    @Override
    @Trace
    public int deleteCustomerByCid(int cid) {
        return customerMapper.deleteCustomerByCid(cid);
    }

    @Trace
    public int addCustomer(CustomerReq customerReq) {
        if (customerReq == null) return -1;
        String idCard = customerReq.getIdCard();
        Customer customer = customerMapper.findCustomerByIdCard(idCard);
        if (customer != null) {
            return 1;
        }
        customer = customerReq.getCustomer();
        return customerMapper.insertCustomer(customer);
    }
}
