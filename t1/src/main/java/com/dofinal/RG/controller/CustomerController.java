package com.dofinal.RG.controller;

import com.dofinal.RG.entity.user.Customer;
import com.dofinal.RG.reqs.entity.CustomerReq;
import com.dofinal.RG.rsps.BaseRsp;
import com.dofinal.RG.service.CustomerService;
import com.dofinal.RG.util.NoticeUtil;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * &#064;Classname CustomerController
 * &#064;Description 注意：这个类其实是用于测试的，在实际应用中，汪汪旅途本身可能不需要这个Controller。
 * &#064;Date 2024/5/16 21:46
 * &#064;Created MuJue
 */
@CrossOrigin
@RestController
public class CustomerController {
    @Autowired
    CustomerService customerService;

    @Autowired
    NoticeUtil noticeUtil;

    /**
     * &#064;description: 这个方法用于获得所有的顾客。顾客和用户的区别将在一份markdown文件中进行详细说明。
     * &#064;author: MuJue
     * &#064;date: 2024/5/17 21:14
     * &#064;param: []
     * &#064;return: BaseRsp<List<Customer>>
     **/
    @PostMapping("/customer/getAll")
    @Trace
    public BaseRsp<List<Customer>> getCustomers() {
        // 直接获得所有的顾客，然后在Response中进行设定。很简单的逻辑。
        List<Customer> customerList = customerService.findCustomers();
        BaseRsp<List<Customer>> rsp = new BaseRsp<>();
        if (customerList != null) {
            rsp.setSuccess(true);
            rsp.setMessage("get all customers success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("get all customers fail!");
        }
        rsp.setContent(customerList);

        return rsp;
    }

    /**
     * &#064;description: 这个方法用于查询在账号为uid的用户拥有的顾客。注意，用户和顾客是多对多关系。
     * &#064;author: MuJue
     * &#064;date: 2024/5/17 21:16
     * &#064;param: [uid]
     * &#064;return: BaseRsp<List<Customer>>
     **/
    @GetMapping("/customer/getByUid/{uid}")
    @Trace
    public BaseRsp<List<Customer>> getCustomerByUid(@PathVariable("uid") String uid) {
        // 通过uid，查找出所有和 以uid为账号的用户 相关的顾客，然后在Response中设置。
        List<Customer> customerList = customerService.findCustomerByUid(uid);
        BaseRsp<List<Customer>> rsp = new BaseRsp<>();
        if (customerList != null) {
            rsp.setSuccess(true);
            rsp.setMessage("get customers by uid success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("get customers by uid fail!");
        }
        rsp.setContent(customerList);
        return rsp;
    }

    @PostMapping("/customer/getCidByTid/{tid}")
    @Trace
    public BaseRsp<List<Integer>> getCidByTid(@PathVariable("tid") String tid) {
        BaseRsp<List<Integer>> rsp = new BaseRsp();
        List<Integer> cids = customerService.getCidByTid(tid);
        if (cids != null) {
            rsp.setSuccess(true);
            rsp.setMessage("get cid by tid success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("get cid by tid fail!");
        }
        rsp.setContent(cids);
        return rsp;
    }

    @PostMapping("/customer/getCidByTidStrict/{tid}")
    @Trace
    public BaseRsp<List<Integer>> getCidByTidStrict(@PathVariable("tid") String tid) {
        BaseRsp<List<Integer>> rsp = new BaseRsp();
        List<Integer> cids = customerService.getCidByTidStrict(tid);
        if (cids != null) {
            rsp.setSuccess(true);
            rsp.setMessage("get cid by tid success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("get cid by tid fail!");
        }
        rsp.setContent(cids);
        return rsp;
    }

    @GetMapping("/customer/getCidByHid/{hid}")
    @Trace
    public BaseRsp<List<Integer>> getCidByHid(@PathVariable("hid") int hid) {
        BaseRsp<List<Integer>> rsp = new BaseRsp();
        List<Integer> cids = customerService.getCidByHid(hid);
        if (cids != null) {
            rsp.setSuccess(true);
            rsp.setMessage("get cid by hid success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("get cid by hid fail!");
        }
        rsp.setContent(cids);
        return rsp;
    }

    /**
     * &#064;description: 直接通过主键(cid)来查询一个顾客。
     * &#064;author: MuJue
     * &#064;date: 2024/5/17 21:18
     * &#064;param: [cid]
     * &#064;return: Customer
     **/
    @GetMapping("/customer/getByCid/{cid}")
    @Trace
    public BaseRsp<Customer> getCustomerByCid(@PathVariable("cid") int cid) {
        // 通过cid查询出顾客，然后在Response中设置。
        Customer customer = customerService.findCustomerById(cid);
        BaseRsp<Customer> rsp = new BaseRsp<>();
        if (customer != null) {
            rsp.setSuccess(true);
            rsp.setMessage("get customer by cid success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("get customer by cid fail!");
        }
        rsp.setContent(customer);
        return rsp;
    }

    @PostMapping("customer/add")
    @Trace
    public BaseRsp addCustomer(@RequestBody CustomerReq req) {
        int res = customerService.addCustomer(req);
        BaseRsp rsp = new BaseRsp();
        if (res != 0) {
            rsp.setSuccess(true);
            rsp.setMessage("add customer success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("add customer false!");
        }
        return rsp;
    }

    // ZHW
    @PutMapping("/customer/update")
    @Trace
    public BaseRsp updateCustomer(@RequestBody CustomerReq req) {
        int res = customerService.updateCustomer(req);
        BaseRsp rsp = new BaseRsp();
        if (res != 0) {
            rsp.setSuccess(true);
            rsp.setMessage("update customer success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("update customer false!");
        }
        return rsp;
    }

    // ZHW
    @DeleteMapping("/customer/deleteByCid{cid}")
    @Trace
    public BaseRsp deleteCustomerByCid(@PathVariable("cid") int cid) {
        int res = customerService.deleteCustomerByCid(cid);
        BaseRsp rsp = new BaseRsp();
        if (res != 0) {
            rsp.setSuccess(true);
            rsp.setMessage("delete customer success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("delete customer false!");
        }
        return rsp;
    }
}
