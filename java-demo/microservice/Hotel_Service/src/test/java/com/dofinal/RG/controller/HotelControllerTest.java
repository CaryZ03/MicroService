package com.dofinal.RG.controller;

import com.dofinal.RG.entity.hotel.Hotel;
import com.dofinal.RG.entity.location.Location;
import com.dofinal.RG.reqs.entity.HotelReq;
import com.dofinal.RG.service.HotelService;
import com.dofinal.RG.service.RoomService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Classname
 * Description TODO
 * Date 2024/8/21 8:58
 * Created ZHW
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@MockBean(ServerEndpointExporter.class)
class HotelControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HotelService hotelService;

    @MockBean
    private RoomService roomService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getHotelByLocationTimeSuccess() throws Exception {
        Location location;
        location = new Location();
        location.setlId(12);
        location.setCity("Beijing");
        location.setProvince("Beijing");

        HotelReq req = new HotelReq();
        req.setLocation(location);
        req.setStartTime(new Timestamp(System.currentTimeMillis()));
        req.setEndTime(new Timestamp(System.currentTimeMillis() + 86400000));

        Hotel hotel = new Hotel(1);
        hotel.setName("Test Hotel");

        Mockito.when(hotelService.findHotelByLocation(Mockito.any(Location.class))).thenReturn(Collections.singletonList(hotel));
        Mockito.when(roomService.findHotelRoomDemoByHidAndTime(Mockito.any(Integer.class), Mockito.any(Timestamp.class), Mockito.any(Timestamp.class), Mockito.any(Timestamp.class))).thenReturn(Collections.emptyList());

        // 执行测试
        mockMvc.perform(post("/hotel/getByLocation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("get hotels by location success!"))
                .andExpect(jsonPath("$.content[0].name").value("Test Hotel"));
    }

    @Test
    public void getHotelByLocationTimeFailure() throws Exception {
        Location location;
        location = new Location();
        location.setlId(12);
        location.setCity("Beijing");
        location.setProvince("Beijing");

        HotelReq req = new HotelReq();
        req.setLocation(location);
        req.setStartTime(new Timestamp(System.currentTimeMillis()));
        req.setEndTime(new Timestamp(System.currentTimeMillis() + 86400000));

        Mockito.when(hotelService.findHotelByLocation(any())).thenReturn(null);

        // 执行测试
        mockMvc.perform(post("/hotel/getByLocation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("get hotels by location fail!"));
    }

    @Test
    public void getHotelByHidSuccess() throws Exception {
        int hid;
        hid = 12;

        Hotel hotel = new Hotel(12);
        hotel.setName("Test Hotel");

        Mockito.when(hotelService.findHotelById(hid)).thenReturn(hotel);

        mockMvc.perform(get("/hotel/getByHid/12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("get hotel by hid success!"))
                .andExpect(jsonPath("$.content.name").value("Test Hotel"));
    }

    @Test
    public void getHotelByHidFailure() throws Exception {
        when(hotelService.findHotelById(12)).thenReturn(null);

        mockMvc.perform(get("/hotel/getByHid/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("get hotel by hid fail!"))
                .andExpect(jsonPath("$.content").doesNotExist());
    }

    @Test
    public void deleteHotelByHidSuccess() throws Exception {
        when(hotelService.deleteHotelByHid(1)).thenReturn(1);

        mockMvc.perform(delete("/hotel/deleteByHid/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("delete hotel success!"));
    }

    @Test
    public void deleteHotelByHidFailure() throws Exception {
        when(hotelService.deleteHotelByHid(1)).thenReturn(0);

        mockMvc.perform(delete("/hotel/deleteByHid/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("delete hotel false!"));
    }
}