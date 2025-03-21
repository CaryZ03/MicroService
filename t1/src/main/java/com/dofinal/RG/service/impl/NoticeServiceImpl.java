package com.dofinal.RG.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dofinal.RG.entity.train.Meal;
import com.dofinal.RG.entity.user.UserNotice;
import com.dofinal.RG.mapper.HotelMapper;
import com.dofinal.RG.mapper.MealMapper;
import com.dofinal.RG.mapper.UserNoticeMapper;
import com.dofinal.RG.reqs.userOrder.OtherUserOrderReq;
import com.dofinal.RG.rsps.Notice.NoticeRsp;
import com.dofinal.RG.service.NoticeService;
import com.dofinal.RG.util.NoticeUtil;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * &#064;Classname NoticeServiceImpl
 * &#064;Description  TODO
 * &#064;Date 2024/5/20 21:09
 * &#064;Created MuJue
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<UserNoticeMapper, UserNotice> implements NoticeService {
    @Autowired
    UserNoticeMapper userNoticeMapper;

    @Autowired
    NoticeUtil noticeUtil;
    @Autowired
    HotelMapper hotelMapper;
    @Autowired
    MealMapper mapper;

    @Override
    @Trace
    public List<UserNotice> getUserNoticeByUid(String uid) {
        if (uid != null) {
            List<UserNotice> res = userNoticeMapper.getUserNoticeByUid(uid);
            if (res == null) return null;
            for (UserNotice userNotice : res) {
                userNotice.setStr_time();
            }
            return res;
        }
        return null;
    }
    private void handleOrderGenerate(NoticeRsp rsp, String uid){
        int oid = rsp.getOid();
        String order = "订单号为" + oid;
        String type = rsp.getOrderType();
        String name = rsp.getMainName();
        double price = rsp.getPrice();
        int ret = rsp.getRet();
        StringBuilder customers = new StringBuilder();
        List<String> customerNames = rsp.getCustomerName();
        for(String customerName : customerNames){
            customers.append(customerName).append(",");
        }
        if(type.equals("hotelOrder")){
            if(ret == 1){
                String content = "您已成功为" + customers + "顾客预定了" + name + "的房间," + order + ",价格为" + price;
                noticeUtil.addNotice(uid, content);
            }
            else {
                String content = name + "的房间预定失败，请重新预定!(人数过多)";
                noticeUtil.addNotice(uid, content);
            }
        }
        else if(type.equals("trainOrder")){
            if (ret == 1){
                String content = "您已成功为" + customers +"顾客预定了" + name + "火车," + order + ",价格为" + price;
                noticeUtil.addNotice(uid, content);
            }
            else {
                String content = "预定" + name + "火车" + "失败，请重新预定!(人数过多)";
                noticeUtil.addNotice(uid, content);
            }
        }
        else if (type.equals("mealOrder")){
            if (ret == 1) {
                String content = "您已成功为" +customers + "顾客预定了" + name + "," + order + ",价格为" + price;
                noticeUtil.addNotice(uid, content);
            }
            else {
                String content = name + "预定失败，请重新预定!(人数过多)";
                noticeUtil.addNotice(uid, content);
            }
        }
    }
    private void handleOrderPay(NoticeRsp rsp, String uid){
        int oid = rsp.getOid();
        String order = "订单号为" + oid;
        String type = rsp.getOrderType();
        int ret = rsp.getRet();
        if(type.equals("hotelOrder")){
            if(ret == 1){
                String content = "您已成功支付" + order + "的订单！(酒店)";
                noticeUtil.addNotice(uid, content);
            }
            else if(ret == -1){
                String content = "支付" + order + "的订单失败(余额不足)";
                noticeUtil.addNotice(uid, content);
            }
            else if(ret == 0){
                String content = "支付" + order + "的订单失败(信息不合法)";
                noticeUtil.addNotice(uid, content);
            }
        }
        else if(type.equals("mealOrder")){
            if(ret == 1){
                String content = "您已成功支付" + order + "的订单！(火车餐)";
                noticeUtil.addNotice(uid, content);
            }
            else if(ret == -1){
                String content = "支付" + order + "的订单失败(余额不足)";
                noticeUtil.addNotice(uid, content);
            }
            else if(ret == 0){
                String content = "支付" + order + "的订单失败(信息不合法)";
                noticeUtil.addNotice(uid, content);
            }
        }
        else if(type.equals("trainOrder")){
            if(ret == 1){
                String content = "您已成功支付" + order + "的订单！(火车)";
                noticeUtil.addNotice(uid, content);
            }
            else if(ret == -1){
                String content = "支付" + order + "的订单失败(余额不足)";
                noticeUtil.addNotice(uid, content);
            }
            else if(ret == 0){
                String content = "支付" + order + "的订单失败(信息不合法)";
                noticeUtil.addNotice(uid, content);
            }
        }
    }
    private void handleOrderCancel(NoticeRsp rsp, String uid){
        int oid = rsp.getOid();
        String order = "订单号为" + oid;
        String type = rsp.getOrderType();
        double price = rsp.getPrice();
        int ret = rsp.getRet();

        if(type.equals("hotelOrder")){
            if(ret == 1){
                String content = "您已成功取消了" + order + "的订单!(酒店)" + price;
                noticeUtil.addNotice(uid, content);
            }
            else {
                String content = "取消" + order + "的订单失败!";
                noticeUtil.addNotice(uid, content);
            }
        }
        else if(type.equals("trainOrder")){
            if(ret == 1){
                String content = "您已成功取消了" + order + "的订单!(火车)" + price;
                noticeUtil.addNotice(uid, content);
            }
            else {
                String content = "取消" + order + "的订单失败!";
                noticeUtil.addNotice(uid, content);
            }
        }
        else if(type.equals("mealOrder")){
            if(ret == 1){
                String content = "您已成功取消了" + order + "的订单!(火车餐)" + price;
                noticeUtil.addNotice(uid, content);
            }
            else {
                String content = "取消" + order + "的订单失败!";
                noticeUtil.addNotice(uid, content);
            }
        }
    }
    private void handleOrderDelete(NoticeRsp rsp, String uid){
        int oid = rsp.getOid();
        String order = "订单号为" + oid;
        String type = rsp.getOrderType();
        double price = rsp.getPrice();
        int ret = rsp.getRet();

        if(type.equals("hotelOrder")){
            if(ret == 1){
                String content = "您已成功删除了" + order + "的订单!(酒店)" + price;
                noticeUtil.addNotice(uid, content);
            }
            else {
                String content = "删除" + order + "的订单失败!";
                noticeUtil.addNotice(uid, content);
            }
        }
        else if(type.equals("trainOrder")){
            if(ret == 1){
                String content = "您已成功删除了" + order + "的订单!(火车)" + price;
                noticeUtil.addNotice(uid, content);
            }
            else {
                String content = "删除" + order + "的订单失败!";
                noticeUtil.addNotice(uid, content);
            }
        }
        else if(type.equals("mealOrder")){
            if(ret == 1){
                String content = "您已成功删除了" + order + "的订单!(火车餐)" + price;
                noticeUtil.addNotice(uid, content);
            }
            else {
                String content = "删除" + order + "的订单失败!";
                noticeUtil.addNotice(uid, content);
            }
        }
    }
    @Override
    @Trace
    public void handleOrderNotice(NoticeRsp rsp, String uid){
        String operationType = rsp.getOperationType();
        if(operationType.equals("generate")){
            handleOrderGenerate(rsp, uid);
        }
        else if(operationType.equals("pay")){
            handleOrderPay(rsp, uid);
        }
        else if(operationType.equals("cancel")){
            handleOrderCancel(rsp, uid);
        }
        else if(operationType.equals("delete")){
            handleOrderDelete(rsp, uid);
        }
    }

}
