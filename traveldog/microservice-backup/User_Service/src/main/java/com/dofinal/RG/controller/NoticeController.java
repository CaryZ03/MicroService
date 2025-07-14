package com.dofinal.RG.controller;

import com.dofinal.RG.entity.user.UserNotice;
import com.dofinal.RG.rsps.BaseRsp;
import com.dofinal.RG.rsps.Notice.NoticeRsp;
import com.dofinal.RG.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * &#064;Classname NoticeController
 * &#064;Description  TODO
 * &#064;Date 2024/5/20 21:10
 * &#064;Created MuJue
 */
@CrossOrigin
@RestController
public class NoticeController {
    @Autowired
    private NoticeService noticeService;
    @GetMapping("/user/getNoticeByUid/{uid}")
    public BaseRsp<List<UserNotice>> getUserNoticeByUid(@PathVariable("uid")String uid){
        BaseRsp<List<UserNotice>> rsp = new BaseRsp<>();
        List<UserNotice> userNotices = noticeService.getUserNoticeByUid(uid);
        if(userNotices != null){
            rsp.setSuccess(true);
            rsp.setMessage("get user notice success!");
        }
        else{
            rsp.setSuccess(false);
            rsp.setMessage("get user notice fail!");
        }
        rsp.setContent(userNotices);
        return rsp;
    }
    @PostMapping("/user/updateNotice/{nid}")
    public BaseRsp updateUserNotice(@PathVariable("nid")Integer nid){
        BaseRsp rsp = new BaseRsp();
        noticeService.updateStatus(nid);
        rsp.setSuccess(true);
        rsp.setMessage("update user notice success!");
        return rsp;
    }
    @PostMapping("/user/deleteNotice/{nid}")
    public BaseRsp deleteUserNotice(@PathVariable("nid")Integer nid){
        BaseRsp rsp = new BaseRsp();
        noticeService.deleteNotice(nid);
        rsp.setSuccess(true);
        rsp.setMessage("delete user notice success!");
        return rsp;
    }
    @PostMapping("/notice/insert")
    Integer insertUserNotice(@RequestBody UserNotice userNotice){
        return noticeService.insertUserNotice(userNotice);
    }

    @PostMapping("/notice/update/nid/{nid}")
    Integer updateStatus(@PathVariable int nid){
        return noticeService.updateStatus(nid);
    }

    @PostMapping("/notice/delete/nid/{nid}")
    Integer deleteNotice(@PathVariable int nid){
        return noticeService.deleteNotice(nid);
    }

    @PostMapping("/notice/handle/")
    void handleOrderNotice(@RequestBody NoticeRsp rsp, @RequestParam("uid") String uid){
        noticeService.handleOrderNotice(rsp, uid);
    }
}
