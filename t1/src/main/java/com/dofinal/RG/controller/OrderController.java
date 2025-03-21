package com.dofinal.RG.controller;

import com.dofinal.RG.entity.hotel.HotelComment;
import com.dofinal.RG.reqs.entity.HotelCommentReq;
import com.dofinal.RG.reqs.userOrder.AddUserOrderReq;
import com.dofinal.RG.reqs.userOrder.OtherUserOrderReq;
import com.dofinal.RG.rsps.BaseRsp;
import com.dofinal.RG.rsps.Notice.NoticeRsp;
import com.dofinal.RG.service.NoticeService;
import com.dofinal.RG.service.OrderService;
import com.dofinal.RG.util.NoticeUtil;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class OrderController {
    @Autowired
    OrderService orderService;

    @Autowired
    NoticeService noticeService;

    @Autowired
    NoticeUtil noticeUtil;

    @PostMapping("/user/addOrder")
    @Trace
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
        noticeService.handleOrderNotice(res, req.getUid());
        return rsp;
    }

    @PostMapping("/user/cancelOrder")
    @Trace
    public BaseRsp cancelUserOrder(@RequestBody OtherUserOrderReq req) {
        BaseRsp rsp = new BaseRsp();
        NoticeRsp res = orderService.cancelUserOrder(req);
        if (res.getRet() == 1) {
            rsp.setSuccess(true);
            rsp.setMessage("delete user order success!");

        } else {
            rsp.setSuccess(false);
            rsp.setMessage("delete user order fail!");
        }
        noticeService.handleOrderNotice(res, req.getUid());
        return rsp;
    }

    @PostMapping("/user/payOrder")
    @Trace
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
        noticeService.handleOrderNotice(res, req.getUid());
        return rsp;
    }

    @PutMapping("user/rateHotelByOid")
    @Trace
    public BaseRsp commentHotel(@RequestBody HotelCommentReq req) {
        int res = orderService.rateHotel(req);
        BaseRsp rsp = new BaseRsp();
        if (res >= 0) {
            rsp.setSuccess(true);
            rsp.setMessage("delete hotel success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("delete hotel false!");
        }
        return rsp;
    }

    @PutMapping("user/getHotelComment")
    @Trace
    public BaseRsp<HotelComment> getHotelComment(@RequestBody HotelCommentReq req) {
        BaseRsp<HotelComment> rsp = new BaseRsp<>();
        HotelComment hotelComment = orderService.getHotelComment(req);
        rsp.setContent(hotelComment);
        rsp.setSuccess(true);
        return rsp;
    }

    @PutMapping("user/deleteHotelComment")
    @Trace
    public BaseRsp deleteHotelComment(@RequestBody HotelCommentReq req) {
        BaseRsp rsp = new BaseRsp();
        orderService.deleteHotelComment(req);
        rsp.setSuccess(true);
        return rsp;
    }

    @PostMapping("/user/deleteOrder")
    @Trace
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
        noticeService.handleOrderNotice(res, req.getUid());
        return rsp;
    }
}
