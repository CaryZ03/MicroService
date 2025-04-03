package com.dofinal.RG.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dofinal.RG.entity.user.UserNotice;
import com.dofinal.RG.rsps.Notice.NoticeRsp;
import org.apache.skywalking.apm.toolkit.trace.Trace;

import java.util.List;

/**
 * &#064;Classname NoticeService
 * &#064;Description  TODO
 * &#064;Date 2024/5/20 21:09
 * &#064;Created MuJue
 */
public interface NoticeService extends IService<UserNotice> {
    @Trace
    List<UserNotice> getUserNoticeByUid(String uid);
    @Trace
    void handleOrderNotice(NoticeRsp rsp, String uid);
}
