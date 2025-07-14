package com.dofinal.RG.controller;

import com.dofinal.RG.entity.user.UserNotice;
import com.dofinal.RG.service.NoticeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @Author: LZX
 * @Date: 2024/8/21 16:12
 */
@SpringBootTest
@AutoConfigureMockMvc
@MockBean(ServerEndpointExporter.class)
class NoticeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NoticeService noticeService;
    @Test
    void getUserNoticeByUidSuccess() throws Exception{
        String uid = "testUid";
        List<UserNotice> userNotices = Collections.singletonList(new UserNotice(1,"testUid",new Timestamp(System.currentTimeMillis()),"content","未读"));

        Mockito.when(noticeService.getUserNoticeByUid(uid)).thenReturn(userNotices);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/getNoticeByUid/{uid}",uid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("get user notice success!"))
                .andExpect(jsonPath("$.content[0].noticeId").value(1));

    }
    @Test
    void getUserNoticeByUidFailure() throws Exception{
        String uid = "testUid";
        List<UserNotice> userNotices = null;

        Mockito.when(noticeService.getUserNoticeByUid(uid)).thenReturn(userNotices);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/getNoticeByUid/{uid}",uid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("get user notice fail!"))
                .andExpect(jsonPath("$.content").value(userNotices));

    }
}