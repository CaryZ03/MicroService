package com.dofinal.RG.util;

import com.dofinal.RG.client.UserClient;
import com.dofinal.RG.entity.user.UserNotice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;

@Component
public class NoticeUtil {
    @Autowired
    UserClient userClient;

    public void addNotice(String uid, String content) {
        UserNotice userNotice = new UserNotice(-1, uid, new Timestamp(System.currentTimeMillis() / 1000 * 1000), content, "未读");
        userClient.insertUserNotice(userNotice);
        userNotice.setNoticeId(userNotice.getNoticeId());
    }

    public void updateNotice(int nid){
        userClient.updateStatus(nid);
    }

    public void deleteNotice(int nid) { userClient.deleteNotice(nid); }
}