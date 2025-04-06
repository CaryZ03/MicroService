package com.dofinal.RG.entity.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * &#064;Classname UserNotice
 * &#064;Description  TODO
 * &#064;Date 2024/5/20 21:06
 * &#064;Created MuJue
 */
@TableName("user_notice")
public class UserNotice {
    @TableField("n_id")
    private int noticeId;
    @TableField("u_id")
    private String userId;
    @TableField("n_time")
    private Timestamp addTime;
    @TableField("n_content")
    private String noticeContent;
    @TableField("n_status")
    private String noticeStatus;
    @TableField(exist = false)
    private String strTime;

    public UserNotice(int noticeId, String userId, Timestamp addTime, String noticeContent, String noticeStatus) {
        this.noticeId = noticeId;
        this.userId = userId;
        this.addTime = addTime;
        this.noticeContent = noticeContent;
        this.noticeStatus = noticeStatus;
    }

    public int getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(int noticeId) {
        this.noticeId = noticeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getAddTime() {
        return addTime;
    }

    public void setAddTime(Timestamp addTime) {
        this.addTime = addTime;
    }

    public String getNoticeContent() {
        return noticeContent;
    }

    public void setNoticeContent(String noticeContent) {
        this.noticeContent = noticeContent;
    }

    public String getStrTime() {
        return strTime;
    }

    public void setStrTime(String strTime) {
        this.strTime = strTime;
    }
    public void setStr_time() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.strTime = df.format(addTime);
    }

    public String getNoticeStatus() {
        return noticeStatus;
    }

    public void setNoticeStatus(String noticeStatus) {
        this.noticeStatus = noticeStatus;
    }
}
