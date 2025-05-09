package com.dofinal.RG.controller;

import com.dofinal.RG.entity.hotel.Hotel;
import com.dofinal.RG.entity.hotel.HotelRoom;
import com.dofinal.RG.service.HotelService;
import com.dofinal.RG.service.RoomService;
import com.dofinal.RG.util.NoticeUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.util.ArrayList;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@MockBean(ServerEndpointExporter.class)
class RoomControllerTest {
    @MockBean
    private RoomService roomService;
    @MockBean
    private HotelService hotelService;
    @MockBean
    private NoticeUtil noticeUtil;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void findHotelRoomByHidSuccess() throws Exception {
        ArrayList<HotelRoom> hotelRooms = new ArrayList<>();
        hotelRooms.add(new HotelRoom(1, 1, "101", new byte[]{1, 1, 1, 1}));
        hotelRooms.add(new HotelRoom(1, 2, "102", new byte[]{1, 0, 1, 1}));
        hotelRooms.add(new HotelRoom(1, 3, "103", new byte[]{1, 1, 0, 1}));

        when(roomService.findHotelRoomByHid(Mockito.anyInt())).thenReturn(hotelRooms);
        mockMvc.perform(get("/room/getByHid/{hid}", 1)).andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("get rooms by hid success!"));

    }

    @Test
    void findHotelRoomByHidFailed() throws Exception {
        when(roomService.findHotelRoomByHid(Mockito.anyInt())).thenReturn(null);
        mockMvc.perform(get("/room/getByHid/{hid}", 2)).andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("get rooms by hid fail!"));

    }

    @Test
    void findHotelRoomByTimeSuccess() throws Exception {
        ArrayList<HotelRoom> hotelRooms = new ArrayList<>();
        hotelRooms.add(new HotelRoom(1, 1, "101", new byte[]{1, 1, 1, 1}));
        hotelRooms.add(new HotelRoom(1, 2, "102", new byte[]{1, 1, 1, 1}));
        hotelRooms.add(new HotelRoom(1, 3, "103", new byte[]{1, 1, 1, 1}));

        when(roomService.findHotelRoomByTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(hotelRooms);
        mockMvc.perform(get("/room/getByTime/").contentType(APPLICATION_JSON).content(
                        "{\n" + "  \"h_id\": null,\n" + "  \"r_id\": null,\n" + "  \"r_number\": null,\n" + "  \"startTime\": \"2024-08-26 12:00:00\",\n" + "  \"endTime\": \"2024-08-27 12:00:00\"\n" + "}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("get rooms by time success!"));

    }

    @Test
    void findHotelRoomByTimeFailed() throws Exception {
        when(roomService.findHotelRoomByTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(null);
        mockMvc.perform(get("/room/getByTime/").contentType(APPLICATION_JSON).content(
                        "{\n" + "  \"h_id\": null,\n" + "  \"r_id\": null,\n" + "  \"r_number\": null,\n" + "  \"startTime\": \"2024-08-26 12:00:00\",\n" + "  \"endTime\": \"2024-08-26 12:00:00\"\n" + "}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("get rooms by time fail!"));
    }

    @Test
    void findHotelRoomByHidAndTimeSuccess() throws Exception {
        ArrayList<HotelRoom> hotelRooms = new ArrayList<>();
        hotelRooms.add(new HotelRoom(1, 1, "101", new byte[]{1, 1, 1, 1}));
        hotelRooms.add(new HotelRoom(1, 2, "102", new byte[]{1, 1, 1, 1}));
        hotelRooms.add(new HotelRoom(1, 3, "103", new byte[]{1, 1, 1, 1}));

        when(roomService.findHotelRoomByHidAndTime(Mockito.anyInt(), Mockito.any(), Mockito.any(),
                Mockito.any())).thenReturn(hotelRooms);
        mockMvc.perform(get("/room/getByHidAndTime/").contentType(APPLICATION_JSON).content(
                        "{\n" + "  \"h_id\": 1,\n" + "  \"r_id\": null,\n" + "  \"r_number\": null,\n" + "  \"startTime\": \"2024-08-26 12:00:00\",\n" + "  \"endTime\": \"2024-08-27 12:00:00\"\n" + "}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("get rooms by hid and time success!"));
    }

    @Test
    void findHotelRoomByHidAndTimeFailed() throws Exception {
        when(roomService.findHotelRoomByHidAndTime(Mockito.anyInt(), Mockito.any(), Mockito.any(),
                Mockito.any())).thenReturn(null);
        mockMvc.perform(get("/room/getByHidAndTime/").contentType(APPLICATION_JSON).content(
                        "{\n" + "  \"h_id\": 2,\n" + "  \"r_id\": null,\n" + "  \"r_number\": null,\n" + "  \"startTime\": \"2024-08-26 12:00:00\",\n" + "  \"endTime\": \"2024-08-27 12:00:00\"\n" + "}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("get rooms by hid and time fail!"));
    }

    @Test
    void findHotelRoomByRidAndTimeSuccess() throws Exception {
        ArrayList<HotelRoom> hotelRooms = new ArrayList<>();
        hotelRooms.add(new HotelRoom(1, 1, "101", new byte[]{1, 1, 1, 1}));
        hotelRooms.add(new HotelRoom(1, 2, "102", new byte[]{1, 1, 1, 1}));
        hotelRooms.add(new HotelRoom(1, 3, "103", new byte[]{1, 1, 1, 1}));

        when(roomService.findHotelRoomByRidAndTime(Mockito.anyInt(), Mockito.any(), Mockito.any(),
                Mockito.any())).thenReturn(hotelRooms);
        mockMvc.perform(get("/room/getByRidAndTime/").contentType(APPLICATION_JSON).content(
                        "{\n" + "  \"h_id\": null,\n" + "  \"r_id\": 1,\n" + "  \"r_number\": null,\n" + "  \"startTime\": \"2024-08-26 12:00:00\",\n" + "  \"endTime\": \"2024-08-27 12:00:00\"\n" + "}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("get rooms by rid and time success!"));

    }

    @Test
    void findHotelRoomByRidAndTimeFailed() throws Exception {
        when(roomService.findHotelRoomByRidAndTime(Mockito.anyInt(), Mockito.any(), Mockito.any(),
                Mockito.any())).thenReturn(null);
        mockMvc.perform(get("/room/getByRidAndTime/").contentType(APPLICATION_JSON).content(
                        "{\n" + "  \"h_id\": null,\n" + "  \"r_id\": 10,\n" + "  \"r_number\": null,\n" + "  \"startTime\": \"2024-08-26 12:00:00\",\n" + "  \"endTime\": \"2024-08-27 12:00:00\"\n" + "}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("get rooms by rid and time fail!"));
    }

    @Test
    void orderRoomSuccess() throws Exception {
        HotelRoom hotelRoom = new HotelRoom(1, 1, "a001", new byte[]{1, 1, 1, 1});

        when(roomService.findHotelRoomByHidRidRNumber(Mockito.anyInt(), Mockito.anyInt(), Mockito.any())).thenReturn(
                hotelRoom);
        when(roomService.checkHotelRoom(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
        when(roomService.orderRoom(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(
                new byte[]{1, 1, 1, 1});
        when(hotelService.findHotelById(Mockito.anyInt())).thenReturn(new Hotel(1, 1, "good-hotel"));
        doNothing().when(noticeUtil).addNotice(Mockito.any(String.class), Mockito.any());
        mockMvc.perform(post("/room/orderRoom").contentType(APPLICATION_JSON).content(
                        "{\n" + "  \"h_id\": 1,\n" + "  \"r_id\": 1,\n" + "  \"r_number\": \"a001\",\n" + "  \"startTime\": \"2024-08-26 12:00:00\",\n" + "  \"endTime\": \"2024-08-27 12:00:00\"\n" + "}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("order room success!"));

    }

    @Test
    void orderRoomFailed() throws Exception {
        HotelRoom hotelRoom = new HotelRoom(1, 1, "a001", new byte[]{1, 1, 1, 1});

        when(roomService.findHotelRoomByHidRidRNumber(Mockito.anyInt(), Mockito.anyInt(), Mockito.any())).thenReturn(
                hotelRoom);
        when(roomService.checkHotelRoom(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(false);
        when(hotelService.findHotelById(Mockito.anyInt())).thenReturn(new Hotel(1, 1, "good-hotel"));
        doNothing().when(noticeUtil).addNotice(Mockito.any(String.class), Mockito.any());
        mockMvc.perform(post("/room/orderRoom").contentType(APPLICATION_JSON).content(
                        "{\n" + "  \"h_id\": 1,\n" + "  \"r_id\": 1,\n" + "  \"r_number\": \"a001\",\n" + "  \"startTime\": \"2024-08-26 12:00:00\",\n" + "  \"endTime\": \"2024-08-26 12:00:00\"\n" + "}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("order room false!"));
    }

    @Test
    void cancelRoomSuccess() throws Exception {
        HotelRoom hotelRoom = new HotelRoom(1, 1, "a001", new byte[]{1, 1, 1, 0});

        when(roomService.findHotelRoomByHidRidRNumber(Mockito.anyInt(), Mockito.anyInt(), Mockito.any())).thenReturn(
                hotelRoom);
        when(roomService.cancelRoom(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(
                new byte[]{1, 1, 1, 0});
        when(roomService.updateHotelRoom(Mockito.any())).thenReturn(1);
        when(hotelService.findHotelById(Mockito.anyInt())).thenReturn(new Hotel(1, 1, "a001"));
        doNothing().when(noticeUtil).addNotice(Mockito.any(String.class), Mockito.any());
        mockMvc.perform(post("/room/cancelRoom").contentType(APPLICATION_JSON).content(
                        "{\n" + "  \"h_id\": 1,\n" + "  \"r_id\": 1,\n" + "  \"r_number\": \"a001\",\n" + "  \"startTime\": \"2024-08-26 12:00:00\",\n" + "  \"endTime\": \"2024-08-27 12:00:00\"\n" + "}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("cancel room success!"));
    }

    @Test
    void cancelRoomFailed() throws Exception {
        HotelRoom hotelRoom = new HotelRoom(1, 1, "a001", new byte[]{1, 1, 1, 0});

        when(roomService.findHotelRoomByHidRidRNumber(Mockito.anyInt(), Mockito.anyInt(), Mockito.any())).thenReturn(
                hotelRoom);
        when(roomService.cancelRoom(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(
                new byte[]{1, 1, 1, 0});
        when(roomService.updateHotelRoom(Mockito.any())).thenReturn(0);
        when(hotelService.findHotelById(Mockito.anyInt())).thenReturn(new Hotel(1, 1, "a001"));
        doNothing().when(noticeUtil).addNotice(Mockito.any(String.class), Mockito.any());
        mockMvc.perform(post("/room/cancelRoom").contentType(APPLICATION_JSON).content(
                        "{\n" + "  \"h_id\": 1,\n" + "  \"r_id\": 1,\n" + "  \"r_number\": \"a001\",\n" + "  \"startTime\": \"2024-08-26 12:00:00\",\n" + "  \"endTime\": \"2024-08-26 12:00:00\"\n" + "}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("cancel room false!"));

    }
}