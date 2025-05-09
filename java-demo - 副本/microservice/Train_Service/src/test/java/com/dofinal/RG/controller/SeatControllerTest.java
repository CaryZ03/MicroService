package com.dofinal.RG.controller;

import com.dofinal.RG.client.LocationClient;
import com.dofinal.RG.entity.location.Location;
import com.dofinal.RG.reqs.entity.SeatReq;
import com.dofinal.RG.service.SeatService;
import com.dofinal.RG.util.NoticeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @Author: LZX
 * @Date: 2024/8/20 22:29
 */
@SpringBootTest
@AutoConfigureMockMvc
@MockBean(ServerEndpointExporter.class)
class SeatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SeatService seatService;

    @MockBean
    private LocationClient locationClient;

    @MockBean
    private NoticeUtil noticeUtil;
    @Test
    void getSeatCountSuccess() throws Exception {
        // 准备测试数据
        SeatReq req = new SeatReq();
        req.setTid("A01");
        req.setSeatType("硬座");

        Location beginLocation = new Location(1,"北京省","北京市");
        Location endLocation = new Location(2,"江苏省", "南京市");
        req.setBeginLocation(beginLocation);
        req.setEndLocation(endLocation);

        // 模拟依赖方法的返回值
        Mockito.when(locationClient.getLidByProvinceCity("北京省","北京市")).thenReturn(1);
        Mockito.when(locationClient.getLidByProvinceCity("江苏省", "南京市")).thenReturn(2);
        Mockito.when(seatService.getSeatCountByTidAndStationIndex("A01", 1, 2, "硬座")).thenReturn(50);

        // 将请求对象转换为 JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(req);

        // 执行测试并验证结果
        mockMvc.perform(MockMvcRequestBuilders.get("/seat/getCount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("get seat count success!"))
                .andExpect(jsonPath("$.content").value(50));


    }

    @Test
    void getSeatCountFailure() throws Exception {
        SeatReq req = new SeatReq();
        req.setTid("A01");
        req.setSeatType("硬座");

        Location beginLocation = new Location(1,"北京省","北京市");
        Location endLocation = new Location(2,"江苏省", "南京市");
        req.setBeginLocation(beginLocation);
        req.setEndLocation(endLocation);

        Mockito.when(locationClient.getLidByProvinceCity("北京省","北京市")).thenReturn(1);
        Mockito.when(locationClient.getLidByProvinceCity("江苏省", "南京市")).thenReturn(2);
        Mockito.when(seatService.getSeatCountByTidAndStationIndex("A01", 1, 2, "硬座")).thenReturn(-1);

        // 将请求对象转换为 JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(req);

        mockMvc.perform(MockMvcRequestBuilders.get("/seat/getCount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("get seat count fail!"))
                .andExpect(jsonPath("$.content").value(-1));
    }

    @Test
    void buySeatSuccess() throws Exception {
        SeatReq req = new SeatReq();
        req.setTid("A01");
        req.setSid(null);
        req.setSeatType("硬座");

        Location beginLocation = new Location(1,"北京省","北京市");
        Location endLocation = new Location(2,"江苏省", "南京市");
        req.setBeginLocation(beginLocation);
        req.setEndLocation(endLocation);
        Mockito.when(locationClient.getLidByProvinceCity("北京省","北京市")).thenReturn(1);
        Mockito.when(locationClient.getLidByProvinceCity("江苏省", "南京市")).thenReturn(2);
        Mockito.when(seatService.buySeatByTidAndStationIndex(req.getTid(),1,2,req.getSeatType())).thenReturn("sb001");

        // 将请求对象转换为 JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(req);

        mockMvc.perform(MockMvcRequestBuilders.post("/seat/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("buy seat success!"))
                .andExpect(jsonPath("$.content").value("sb001"));
    }

    @Test
    void buySeatFailure() throws Exception {
        SeatReq req = new SeatReq();
        req.setTid("A01");
        req.setSid(null);
        req.setSeatType("硬座");

        Location beginLocation = new Location(1,"北京省","北京市");
        Location endLocation = new Location(2,"江苏省", "南京市");
        req.setBeginLocation(beginLocation);
        req.setEndLocation(endLocation);
        Mockito.when(locationClient.getLidByProvinceCity("北京省","北京市")).thenReturn(1);
        Mockito.when(locationClient.getLidByProvinceCity("江苏省", "南京市")).thenReturn(2);
        Mockito.when(seatService.buySeatByTidAndStationIndex(req.getTid(),1,2,req.getSeatType())).thenReturn(null);

        // 将请求对象转换为 JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(req);

        mockMvc.perform(MockMvcRequestBuilders.post("/seat/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("buy seat fail!"))
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void cancelSeatSuccess() throws Exception {
        SeatReq req = new SeatReq();
        req.setTid("A01");
        req.setSid("sb001");
        req.setSeatType("硬座");

        Location beginLocation = new Location(1,"北京省","北京市");
        Location endLocation = new Location(2,"江苏省", "南京市");
        req.setBeginLocation(beginLocation);
        req.setEndLocation(endLocation);
        Mockito.when(locationClient.getLidByProvinceCity("北京省","北京市")).thenReturn(1);
        Mockito.when(locationClient.getLidByProvinceCity("江苏省", "南京市")).thenReturn(2);
        Mockito.when(seatService.cancelSeatByTidAndStationIndex(req.getTid(),req.getSid(),1,2)).thenReturn(true);

        // 将请求对象转换为 JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(req);

        mockMvc.perform(MockMvcRequestBuilders.post("/seat/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("cancel seat success!"))
                .andExpect(jsonPath("$.content").value(true));
    }

    @Test
    void cancelSeatFailure() throws Exception {
        SeatReq req = new SeatReq();
        req.setTid("A01");
        req.setSid("sb001");
        req.setSeatType("硬座");

        Location beginLocation = new Location(1,"北京省","北京市");
        Location endLocation = new Location(2,"江苏省", "南京市");
        req.setBeginLocation(beginLocation);
        req.setEndLocation(endLocation);
        Mockito.when(locationClient.getLidByProvinceCity("北京省","北京市")).thenReturn(1);
        Mockito.when(locationClient.getLidByProvinceCity("江苏省", "南京市")).thenReturn(2);
        Mockito.when(seatService.cancelSeatByTidAndStationIndex(req.getTid(),req.getSid(),1,2)).thenReturn(false);

        // 将请求对象转换为 JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(req);

        mockMvc.perform(MockMvcRequestBuilders.post("/seat/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("cancel seat fail!"))
                .andExpect(jsonPath("$.content").value(false));
    }
}