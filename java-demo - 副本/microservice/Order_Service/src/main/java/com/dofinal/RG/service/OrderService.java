package com.dofinal.RG.service;

import com.dofinal.RG.entity.hotel.HotelComment;
import com.dofinal.RG.entity.order.HotelCustomerOrder;
import com.dofinal.RG.entity.order.TrainCustomerOrder;
import com.dofinal.RG.entity.order.UserOrder;
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

    NoticeRsp generateUserOrder(AddUserOrderReq req);// 生成用户订单

    NoticeRsp payUserOrder(OtherUserOrderReq req) throws PurchaseException; // 支付用户订单

    NoticeRsp cancelUserOrder(OtherUserOrderReq req); // 取消用户订单

    NoticeRsp deleteUserOrder(OtherUserOrderReq req);// 删除用户订单

    List<UserOrderDemo> getUserOrderDemoByUid(String uid);

    UserOrder getUserOrderByOid(Integer oid);

    List<Integer> getOrderIdsByTid(String tid);

    List<TrainCustomerOrder> findTrainOrderByOid(Integer oid);

    List<TrainCustomerOrder> findTrainOrderByTid(String tid);

    List<HotelCustomerOrder> findHotelOrderByHid(Integer hid);

    List<HotelCustomerOrder> findHotelOrderByOid(Integer oid);

    List<Integer> getOidByUid(String uid);

    Integer deleteHotelOderByOid(Integer oid);

    Integer deleteMealOrderByOid(Integer oid);

    Integer deleteTrainOrderByOid(Integer oid);

    Integer deleteUserOrderByUid(String uid);

    List<HotelComment> getHotelCommentByHid(int hid) ;

    List<UserOrder> getUserOrderByUid(String uid);
}
