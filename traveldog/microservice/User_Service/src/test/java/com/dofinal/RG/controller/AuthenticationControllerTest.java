package com.dofinal.RG.controller;

import com.dofinal.RG.entity.user.User;
import com.dofinal.RG.exceptions.AuthenticationException;
import com.dofinal.RG.reqs.entity.AuthenticationReq;
import com.dofinal.RG.service.AuthenticationService;
import com.dofinal.RG.util.NoticeUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Classname
 * Description TODO
 * Date 2024/8/20 14:51
 * Created ZHW
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@MockBean(ServerEndpointExporter.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private NoticeUtil noticeUtil;

    @Test
    public void loginSuccess() throws Exception {
        AuthenticationReq req = new AuthenticationReq();
        req.setUid("testuser");
        req.setPassword("password123");

        User user = new User("testuser", "password123", "在线");

        Mockito.when(authenticationService.login(Mockito.any(AuthenticationReq.class))).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"uid\":\"testuser\", \"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("login success!"));
    }

    @Test
    public void loginFailure() throws Exception {
        AuthenticationReq req = new AuthenticationReq();
        req.setUid("haha128");
        req.setPassword("12345678");

        Mockito.when(authenticationService.login(Mockito.any(AuthenticationReq.class))).thenThrow(new AuthenticationException("密码错误"));

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"uid\":\"testuser\", \"password\":\"wrong password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("密码错误"));
    }

    @Test
    public void registerSuccess() throws Exception {
        AuthenticationReq req = new AuthenticationReq();
        req.setUid("newuser");
        req.setPassword("password123");

        Mockito.doNothing().when(authenticationService).register(Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"uid\":\"newuser\", \"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("register success! Now you can login!"));
    }

    @Test
    public void registerFailure() throws Exception {
        AuthenticationReq req = new AuthenticationReq();
        req.setUid("user");
        req.setPassword("password123");

        Mockito.doThrow(new AuthenticationException("用户已注册"))
                        .when(authenticationService).register(Mockito.any(AuthenticationReq.class));


        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"uid\":\"user\", \"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户已注册"));
    }

    @Test
    public void logoutSuccess() throws Exception{
        AuthenticationReq req = new AuthenticationReq();
        req.setUid("testuser");

        mockMvc.perform(MockMvcRequestBuilders.post("/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"uid\":\"testuser\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("logout success!"));
    }
}