package com.dofinal.RG.controller;

import com.dofinal.RG.client.UserClient;
import com.dofinal.RG.entity.hotel.HotelComment;
import com.dofinal.RG.exceptions.PurchaseException;
import com.dofinal.RG.rsps.Notice.NoticeRsp;
import com.dofinal.RG.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
class OrderControllerTest {
    private static final Logger log = LoggerFactory.getLogger(OrderControllerTest.class);
    @MockBean
    OrderService orderService;
    @MockBean
    UserClient userClient;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void addUserOrderSuccess() throws Exception {
        List<String> names = new ArrayList<String>();
        names.add("John");
        names.add("Jack");
        when(orderService.generateUserOrder(Mockito.any())).thenReturn(
                new NoticeRsp(names, "generate", "mealOrder", 0, 1));
        mockMvc.perform(post("/user/addOrder/").contentType(APPLICATION_JSON).content(
                        "{\n" + "  \"uid\": \"haha128\",\n" + "  \"orderType\": \"mealOrder\",\n" + "  \"tor\": null," + "\n" + "  \"mor\": {\n" + "    \"cids\": [\n" + "      3\n" + "    ],\n" + "    \"tid\": \"A01\",\n" + "    \"mid\": 3\n" + "  },\n" + "  \"hor\": null\n" + "}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("add user order success!"));
    }

    @Test
    void addUserOrderFail() throws Exception {
        List<String> names = new ArrayList<String>();
        names.add("John");
        names.add("Jack");
        when(orderService.generateUserOrder(Mockito.any())).thenReturn(
                new NoticeRsp(names, "generate", "mealOrder", 0, 0));
        doNothing().when(userClient).handleOrderNotice(Mockito.any(), Mockito.any());
        mockMvc.perform(post("/user/addOrder/").contentType(APPLICATION_JSON).content(
                        "{\n" + "  \"uid\": \"hehe\",\n" + "  \"orderType\": \"mealOrder\",\n" + "  \"tor\": null,\n" + "  \"mor\": {\n" + "    \"cids\": [\n" + "      3\n" + "    ],\n" + "    \"tid\": \"A01\",\n" + "    \"mid\": 3\n" + "  },\n" + "  \"hor\": null\n" + "}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("add user order fail!"));

    }

    @Test
    void cancelUserOrderSuccess() throws Exception {
        List<String> names = new ArrayList<String>();
        names.add("John");
        names.add("Jack");
        when(orderService.cancelUserOrder(Mockito.any())).thenReturn(new NoticeRsp(names, "cancel", "mealOrder", 7, 1));
        mockMvc.perform(post("/user/cancelOrder/").contentType(APPLICATION_JSON)
                        .content("{\n" + "  \"uid\": \"haha128\",\n" + "  \"oid\": 7\n" + "}")).andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("cancel user order success!"));
    }

    @Test
    void cancelUserOrderFail() throws Exception {
        List<String> names = new ArrayList<String>();
        names.add("John");
        names.add("Jack");
        when(orderService.cancelUserOrder(Mockito.any())).thenReturn(new NoticeRsp(names, "cancel", "mealOrder", 7, 0));
        doNothing().when(userClient).handleOrderNotice(Mockito.any(), Mockito.any());
        mockMvc.perform(post("/user/cancelOrder/").contentType(APPLICATION_JSON)
                        .content("{\n" + "  \"uid\": \"hehe\",\n" + "  \"oid\": 7\n" + "}")).andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("cancel user order fail!"));
    }

    @Test
    void payUserOrderSuccess() throws Exception {
        List<String> names = new ArrayList<String>();
        names.add("John");
        names.add("Jack");
        when(orderService.payUserOrder(Mockito.any())).thenReturn(new NoticeRsp(names, "pay", "mealOrder", 4, 1));
        mockMvc.perform(post("/user/payOrder/").contentType(APPLICATION_JSON)
                        .content("{\n" + "  \"uid\": \"haha128\",\n" + "  \"oid\": 4\n" + "}")).andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("pay user order success!"));
    }

    @Test
    void payUserOrderFail() throws Exception {
        List<String> names = new ArrayList<String>();
        names.add("John");
        names.add("Jack");
        when(orderService.payUserOrder(Mockito.any())).thenThrow(new PurchaseException("pay user order fail!"));
        doNothing().when(userClient).handleOrderNotice(Mockito.any(), Mockito.any());
        mockMvc.perform(post("/user/payOrder/").contentType(APPLICATION_JSON)
                        .content("{\n" + "  \"uid\": \"hehe\",\n" + "  \"oid\": 4\n" + "}")).andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("pay user order fail!"));
    }

    @Test
    void commentHotelSuccess() throws Exception {
        when(orderService.rateHotel(Mockito.any())).thenReturn(0);
        mockMvc.perform(put("/user/rateHotelByOid/").contentType(APPLICATION_JSON).content(
                        "{\n" + "    \"uid\":1,\n" + "    \"oid\":1,\n" + "    \"rate\":5,\n" + "    \"comment\":\"good\"\n" + "}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("comment hotel success!"));
    }

    @Test
    void commentHotelFail() throws Exception {
        when(orderService.rateHotel(Mockito.any())).thenReturn(-1);
        mockMvc.perform(put("/user/rateHotelByOid/").contentType(APPLICATION_JSON).content(
                        "{\n" + "    \"uid\":1,\n" + "    \"oid\":1,\n" + "    \"rate\":6,\n" + "    \"comment\":\"good\"\n" + "}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("comment hotel false!"));

    }

    @Test
    void getHotelCommentSuccess() throws Exception {
        when(orderService.getHotelComment(Mockito.any())).thenReturn(new HotelComment(-1,1, 5, 5, "good"));
        mockMvc.perform(
                        post("/user/getHotelComment/").contentType(APPLICATION_JSON).content("{\n" + "    \"oid\":1\n" + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

    }


    @Test
    void deleteHotelCommentSuccess() throws Exception {
        when(orderService.deleteHotelComment(Mockito.any())).thenReturn(1);
        mockMvc.perform(
                        post("/user/deleteHotelComment/").contentType(APPLICATION_JSON).content("{\n" + "    \"oid\":1\n" + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }


    @Test
    void deleteUserOrderSuccess() throws Exception {
        List<String> names = new ArrayList<String>();
        names.add("John");
        names.add("Jack");
        when(orderService.deleteUserOrder(Mockito.any())).thenReturn(new NoticeRsp(names, "delete", "mealOrder", 4, 1));
        mockMvc.perform(
                        post("/user/deleteOrder/").contentType(APPLICATION_JSON).content("{\n" + "  \"uid\": \"haha128\",\n" + "  \"oid\": 12\n" + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("delete user order success!"));
    }

    @Test
    void deleteUserOrderFail() throws Exception {
        List<String> names = new ArrayList<String>();
        names.add("John");
        names.add("Jack");
        when(orderService.deleteUserOrder(Mockito.any())).thenReturn(new NoticeRsp(names, "delete", "mealOrder", 4, 0));
        mockMvc.perform(
                        post("/user/deleteOrder/").contentType(APPLICATION_JSON).content("{\n" + "  \"uid\": \"hehe\",\n" + "  \"oid\": 12\n" + "}")).andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("delete user order fail!"));
    }
}