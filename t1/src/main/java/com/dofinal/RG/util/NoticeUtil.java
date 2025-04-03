package com.dofinal.RG.util;

import com.dofinal.RG.entity.user.UserNotice;
import com.dofinal.RG.mapper.UserNoticeMapper;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Timestamp;

@Component
public class NoticeUtil {
    @Autowired
    UserNoticeMapper userNoticeMapper;

    @Trace
    public void addNotice(String uid, String content) {
        UserNotice userNotice = new UserNotice(-1, uid, new Timestamp(System.currentTimeMillis() / 1000 * 1000), content, "未读");
        userNoticeMapper.insertUserNotice(userNotice);
        userNotice.setNoticeId(userNotice.getNoticeId());
        try {
            WebSocketServer.sendCustomInfo(userNotice, uid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Trace
    public void updateNotice(int nid){
        userNoticeMapper.updateStatus(nid);
    }

    @Trace
    public void deleteNotice(int nid) { userNoticeMapper.deleteNotice(nid); }
}