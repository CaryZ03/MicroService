package com.dofinal.RG.controller;

import com.dofinal.RG.entity.user.UserNotice;
import com.dofinal.RG.rsps.BaseRsp;
import com.dofinal.RG.service.NoticeService;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
    @Trace
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
}
