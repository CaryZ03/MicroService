package com.dofinal.RG.service;

import com.dofinal.RG.entity.hotel.HotelComment;
import com.dofinal.RG.entity.order.UserOrderDemo;
import com.dofinal.RG.exceptions.PurchaseException;
import com.dofinal.RG.reqs.entity.HotelCommentReq;
import com.dofinal.RG.reqs.userOrder.AddUserOrderReq;
import com.dofinal.RG.reqs.userOrder.OtherUserOrderReq;
import com.dofinal.RG.rsps.Notice.NoticeRsp;
import java.util.List;

/**
 * &#064;Classname OrderService
 * &#064;Description TODO
 * &#064;Date 2024/6/2 17:47
 * &#064;Created MuJue
 */
public interface OrderService {

    int rateHotel(HotelCommentReq req);

    HotelComment getHotelComment(HotelCommentReq req);

    int deleteHotelComment(HotelCommentReq req);

    // 生成用户订单
    NoticeRsp generateUserOrder(AddUserOrderReq req);

    // 支付用户订单
    NoticeRsp payUserOrder(OtherUserOrderReq req) throws PurchaseException;

    // 取消用户订单
    NoticeRsp cancelUserOrder(OtherUserOrderReq req);

    // 删除用户订单
    NoticeRsp deleteUserOrder(OtherUserOrderReq req);

    List<UserOrderDemo> getUserOrdersByUid(String uid);
}
