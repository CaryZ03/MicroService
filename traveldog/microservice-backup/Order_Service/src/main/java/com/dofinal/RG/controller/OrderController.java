package com.dofinal.RG.controller;

import com.dofinal.RG.client.UserClient;
import com.dofinal.RG.entity.hotel.HotelComment;

import com.dofinal.RG.entity.order.HotelCustomerOrder;
import com.dofinal.RG.entity.order.TrainCustomerOrder;
import com.dofinal.RG.entity.order.UserOrder;
import com.dofinal.RG.entity.order.UserOrderDemo;

import com.dofinal.RG.reqs.entity.HotelCommentReq;
import com.dofinal.RG.reqs.userOrder.AddUserOrderReq;
import com.dofinal.RG.reqs.userOrder.OtherUserOrderReq;
import com.dofinal.RG.rsps.BaseRsp;
import com.dofinal.RG.rsps.Notice.NoticeRsp;
import com.dofinal.RG.service.OrderService;
import com.dofinal.RG.util.NoticeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
public class OrderController {
    @Autowired
    OrderService orderService;

    @Autowired
    UserClient userClient;

    @Autowired
    NoticeUtil noticeUtil;

    @PostMapping("/user/addOrder")
    public BaseRsp addUserOrder(@RequestBody AddUserOrderReq req) {
        BaseRsp rsp = new BaseRsp();
        NoticeRsp res = orderService.generateUserOrder(req);
        if (res.getRet() == 1) {
            rsp.setSuccess(true);
            rsp.setMessage("add user order success!");

        } else {
            rsp.setSuccess(false);
            rsp.setMessage("add user order fail!");
        }
        userClient.handleOrderNotice(res, req.getUid());
        return rsp;
    }

    @PostMapping("/user/cancelOrder")
    public BaseRsp cancelUserOrder(@RequestBody OtherUserOrderReq req) {
        BaseRsp rsp = new BaseRsp();
        NoticeRsp res = orderService.cancelUserOrder(req);
        if (res.getRet() == 1) {
            rsp.setSuccess(true);
            rsp.setMessage("cancel user order success!");

        } else {
            rsp.setSuccess(false);
            rsp.setMessage("cancel user order fail!");
        }
        userClient.handleOrderNotice(res, req.getUid());
        return rsp;
    }

    @PostMapping("/user/payOrder")
    public BaseRsp payUserOrder(@RequestBody OtherUserOrderReq req) {
        BaseRsp rsp = new BaseRsp();
        NoticeRsp res;
        try {
            res = orderService.payUserOrder(req);
            rsp.setSuccess(true);
            rsp.setMessage("pay user order success!");
        } catch (com.dofinal.RG.exceptions.PurchaseException e) {
            res = new NoticeRsp();
            res.setOid(req.getOid());

            rsp.setSuccess(false);
            rsp.setMessage(e.getMessage());
        }
        userClient.handleOrderNotice(res, req.getUid());
        return rsp;
    }

    @PutMapping("user/rateHotelByOid")
    public BaseRsp commentHotel(@RequestBody HotelCommentReq req) {
        int res = orderService.rateHotel(req);
        BaseRsp rsp = new BaseRsp();
        if (res >= 0) {
            rsp.setSuccess(true);
            rsp.setMessage("comment hotel success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("comment hotel false!");
        }
        return rsp;
    }

    @PostMapping("user/getHotelComment")
    public BaseRsp<HotelComment> getHotelComment(@RequestBody HotelCommentReq req) {
        BaseRsp<HotelComment> rsp = new BaseRsp<>();
        HotelComment hotelComment = orderService.getHotelComment(req);
        rsp.setContent(hotelComment);
        rsp.setSuccess(true);
        return rsp;
    }

    @PostMapping("user/deleteHotelComment")
    public BaseRsp deleteHotelComment(@RequestBody HotelCommentReq req) {
        BaseRsp rsp = new BaseRsp();
        orderService.deleteHotelComment(req);
        rsp.setSuccess(true);
        return rsp;
    }

    @PostMapping("/user/deleteOrder")
    public BaseRsp deleteUserOrder(@RequestBody OtherUserOrderReq req) {
        BaseRsp rsp = new BaseRsp();
        NoticeRsp res = orderService.deleteUserOrder(req);
        if (res.getRet() == 1) {
            rsp.setSuccess(true);
            rsp.setMessage("delete user order success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("delete user order fail!");
        }
        userClient.handleOrderNotice(res, req.getUid());
        return rsp;
    }

    @GetMapping("/order/getHotelCommentByHid/{hid}")
    List<HotelComment> getHotelCommentByHid(@PathVariable int hid){
        return orderService.getHotelCommentByHid(hid);
    }

    @GetMapping("/order/getUserOrderByUid/{uid}")
    public List<UserOrder> getUserOrderByUid(@PathVariable String uid){
        return orderService.getUserOrderByUid(uid);
    }

    @GetMapping("/order/getUserOrderByOid/{oid}")
    UserOrder getUserOrderByOid(@PathVariable Integer oid){
        return orderService.getUserOrderByOid(oid);
    }

    @GetMapping("/order/getOidByTid/{tid}")
    List<Integer> getOrderIdsByTid(@PathVariable String tid){
        return orderService.getOrderIdsByTid(tid);
    }

    @GetMapping("/order/findTrainOrderByOid/{oid}")
    List<TrainCustomerOrder> findTrainOrderByOid(@PathVariable Integer oid){
        return orderService.findTrainOrderByOid(oid);
    }

    @GetMapping("/order/getUserOrderDemoByUid/{uid}")
    List<UserOrderDemo> getUserOrderDemoByUid(@PathVariable String uid){
        return orderService.getUserOrderDemoByUid(uid);
    }

    @GetMapping("/order/findTrainOrderByTid/{tid}")
    List<TrainCustomerOrder> findTrainOrderByTid(@PathVariable String tid){
        return orderService.findTrainOrderByTid(tid);
    }

    @GetMapping("/order/findHotelOrderByHid/{hid}")
    List<HotelCustomerOrder> findHotelOrderByHid(@PathVariable Integer hid){
        return orderService.findHotelOrderByHid(hid);
    }

    @GetMapping("/order/findHotelOrderByOid/{oid}")
    List<HotelCustomerOrder> findHotelOrderByOid(@PathVariable Integer oid){
        return orderService.findHotelOrderByOid(oid);
    }

    @GetMapping("/order/getOidByUid/{uid}")
    List<Integer> getOidByUid(@PathVariable String uid){
        return orderService.getOidByUid(uid);
    }

    @GetMapping("/order/deleteHotelOrderByOid/{oid}")
    Integer deleteHotelOderByOid(@PathVariable Integer oid){
        return orderService.deleteHotelOderByOid(oid);
    }

    @GetMapping("/order/deleteMealOrderByOid/{oid}")
    Integer deleteMealOrderByOid(@PathVariable Integer oid){
        return orderService.deleteMealOrderByOid(oid);
    }

    @GetMapping("/order/deleteTrainOrderByOid/{oid}")
    Integer deleteTrainOrderByOid(@PathVariable Integer oid){
        return orderService.deleteTrainOrderByOid(oid);
    }

    @GetMapping("/order/deleteUserOrderByOid/{uid}")
    Integer deleteUserOrderByUid(@PathVariable String uid){
        return orderService.deleteUserOrderByUid(uid);
    }

}
