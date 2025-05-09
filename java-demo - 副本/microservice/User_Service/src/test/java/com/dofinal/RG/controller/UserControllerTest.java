package com.dofinal.RG.controller;

import com.dofinal.RG.client.OrderClient;
import com.dofinal.RG.entity.location.Location;
import com.dofinal.RG.entity.order.HotelCustomerOrderDemo;
import com.dofinal.RG.entity.order.MealCustomerOrderDemo;
import com.dofinal.RG.entity.order.TrainCustomerOrderDemo;
import com.dofinal.RG.entity.order.UserOrderDemo;
import com.dofinal.RG.entity.user.Customer;
import com.dofinal.RG.entity.user.User;
import com.dofinal.RG.reqs.entity.CustomerReq;
import com.dofinal.RG.reqs.entity.UserReq;
import com.dofinal.RG.service.UserService;
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
import org.springframework.util.DigestUtils;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * &#064;Classname UserControllerTest
 * &#064;Description  TODO
 * &#064;Date 2024/8/20 21:46
 * &#064;Created MuJue
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@MockBean(ServerEndpointExporter.class)
class UserControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserService userService;
    @MockBean
    private OrderClient orderClient;

    @Test
    void getUserByUidSuccess() throws Exception {
        User user = new User("haha128","CNM","12345678","13000000000",
                1000.0, "离线");

        Mockito.when(userService.findUserByUid(user.getId())).thenReturn(user);

        mvc.perform(MockMvcRequestBuilders.get("/user/getByUid/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("get user by id success!"));
    }

    @Test
    void getUserByUidFailed() throws Exception {
        User user = null;

        Mockito.when(userService.findUserByUid(Mockito.any())).thenReturn(user);

        mvc.perform(MockMvcRequestBuilders.get("/user/getByUid/" + -1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("get user by id fail!"));
    }

    @Test
    void getUserCustomerByUidSuccess() throws Exception{
        String uid = "haha128";

        List<Customer> customers = new ArrayList<>();

        customers.add(new Customer(1, "sb","13000000000","520000000000000000", 20));
        customers.add(new Customer(2, "hp","13000000000","520000000000000000", 20));
        customers.add(new Customer(3, "nt","13000000000","520000000000000000", 20));

        Mockito.when(userService.getCustomersByUid(uid)).thenReturn(customers);

        mvc.perform(MockMvcRequestBuilders.get("/user/getUserCustomer/"+uid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("get customers by uid success!"));
    }

    @Test
    void getUserCustomerByUidFailed() throws Exception{
        String uid = "haha128";

        List<Customer> customers = null;

        Mockito.when(userService.getCustomersByUid(uid)).thenReturn(customers);

        mvc.perform(MockMvcRequestBuilders.get("/user/getUserCustomer/"+uid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("get customers by uid fail!"));
    }

    @Test
    void getUserOrderByUidSuccess() throws Exception {
        String uid = "haha128";
        List<UserOrderDemo> userOrderDemoList = new ArrayList<>();

        userOrderDemoList.add(new UserOrderDemo(1, "haha128","hotelOrder",
                new Timestamp(System.currentTimeMillis()), "not paid", 200.0, false));
        userOrderDemoList.add(new UserOrderDemo(2, "haha128","trainOrder",
                new Timestamp(System.currentTimeMillis()), "not paid", 200.0, false));
        userOrderDemoList.add(new UserOrderDemo(3, "haha128","mealOrder",
                new Timestamp(System.currentTimeMillis()), "not paid", 200.0, false));

        Mockito.when(orderClient.getUserOrdersByUid(uid)).thenReturn(userOrderDemoList);

        mvc.perform(MockMvcRequestBuilders.get("/user/getUserOrder/" + uid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("get userOrders by uid success!"));

    }

    @Test
    void getUserOrderByUidFailed() throws Exception {
        String uid = "haha128";
        List<UserOrderDemo> userOrderDemoList = null;

        Mockito.when(orderClient.getUserOrdersByUid(uid)).thenReturn(userOrderDemoList);

        mvc.perform(MockMvcRequestBuilders.get("/user/getUserOrder/" + uid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("get userOrders by uid fail!"));

    }

    @Test
    void getUserWithOrderAndCustomerByUidSuccess() throws Exception{
        User user = new User("haha128","dick","12345678",
                "13000000000",2000.0, "离线");
        List<UserOrderDemo> userOrderDemoList = new ArrayList<>();
        List<TrainCustomerOrderDemo> trainCustomerOrderDemos = new ArrayList<>();
        List<MealCustomerOrderDemo> mealCustomerOrderDemos = new ArrayList<>();
        List<HotelCustomerOrderDemo> hotelCustomerOrderDemos = new ArrayList<>();

        trainCustomerOrderDemos.add(new TrainCustomerOrderDemo(
                1, "sb", 20, "sdsa", "dsadsa","dsdsa",
                new Location(1, "北京市","北京省"),
                new Location(2, "江苏省","南京市"),
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis() + 1000)
        ));

        mealCustomerOrderDemos.add(new MealCustomerOrderDemo(
                "sdsa",1,"sb",20.0, "红烧大鹅"
        ));

        hotelCustomerOrderDemos.add(new HotelCustomerOrderDemo(1, "sb", 20.0,
                "cnm-hotel","102",new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis() + 1000)));

        userOrderDemoList.add(new UserOrderDemo(1, "haha128","hotelOrder",
                new Timestamp(System.currentTimeMillis()), "not paid", 200.0, false));
        userOrderDemoList.add(new UserOrderDemo(2, "haha128","trainOrder",
                new Timestamp(System.currentTimeMillis()), "not paid", 200.0, false));
        userOrderDemoList.add(new UserOrderDemo(3, "haha128","mealOrder",
                new Timestamp(System.currentTimeMillis()), "not paid", 200.0, false));

        userOrderDemoList.get(0).setHotelCustomerOrderDemos(hotelCustomerOrderDemos);
        userOrderDemoList.get(1).setTrainCustomerOrderDemos(trainCustomerOrderDemos);
        userOrderDemoList.get(2).setMealCustomerOrderDemos(mealCustomerOrderDemos);

        Mockito.when(userService.findUserWithOrderAndCustomerByUid(user.getId())).thenReturn(user);

        mvc.perform(MockMvcRequestBuilders.get("/user/getAllInfo/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("get user all info by id success!"));
    }

    @Test
    void getUserWithOrderAndCustomerByUidFailed() throws Exception{
        Mockito.when(userService.findUserWithOrderAndCustomerByUid(Mockito.any())).thenReturn(null);

        mvc.perform(MockMvcRequestBuilders.get("/user/getAllInfo/CNM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("get user all info by id fail!"));
    }

    @Test
    void getUsersSuccess() throws Exception{
        List<User> users = new ArrayList<>();
        users.add(new User("haha128","dick","12345678",
                "13000000000",2000.0, "离线"));
        users.add(new User("mujue37","dick","12345678",
                "13000000000",2000.0, "离线"));
        users.add(new User("xixi255","dick","12345678",
                "13000000000",2000.0, "离线"));

        Mockito.when(userService.findUsers()).thenReturn(users);

        mvc.perform(MockMvcRequestBuilders.get("/user/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("get all users success!"));
    }

    @Test
    void getUsersFailed() throws Exception{
        List<User> users = null;

        Mockito.when(userService.findUsers()).thenReturn(users);

        mvc.perform(MockMvcRequestBuilders.get("/user/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("get all users fail!"));
    }

    @Test
    void deleteUserByIdSuccess() throws Exception{
        String uid = "haha128";

        Mockito.when(userService.deleteUserByUid(uid)).thenReturn(1);

        mvc.perform(MockMvcRequestBuilders.delete("/user/deleteByUid/"+ uid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("delete user by uid success!"));
    }

    @Test
    void deleteUserByIdFailed() throws Exception{
        String uid = "haha128";

        Mockito.when(userService.deleteUserByUid(uid)).thenReturn(0);

        mvc.perform(MockMvcRequestBuilders.delete("/user/deleteByUid/"+ uid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("delete user by uid fail!"));
    }

    @Test
    void updateUserSuccess() throws Exception{
        UserReq req = new UserReq(
                "haha128",
                "12345678",
                "13000000000",
                "dick",
                2000.0,
                "离线"
        );
        User user = req.getUser();

        Mockito.when(userService.updateUser(Mockito.any(UserReq.class))).thenReturn(1);
        Mockito.when(userService.findUserByUid(Mockito.any())).thenReturn(user);

        mvc.perform(MockMvcRequestBuilders.put("/user/update/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"uid\": \"haha128\",\n" +
                        "    \"password\": \"12345678\",\n" +
                        "    \"phoneNumber\": \"13000000000\",\n" +
                        "    \"name\": \"dick\",\n" +
                        "    \"money\": 2000.0,\n" +
                        "    \"status\":\"离线\"\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("update user success!"));
    }

    @Test
    void updateUserFailed() throws Exception{
        UserReq req = new UserReq(
                "haha128",
                "12345678",
                "13000000000",
                "dick",
                2000.0,
                "离线"
        );
        User user = req.getUser();
        String originPassword = req.getPassword();
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));

        Mockito.when(userService.updateUser(Mockito.any(UserReq.class))).thenReturn(0);
        Mockito.when(userService.findUserByUid(Mockito.any())).thenReturn(user);

        mvc.perform(MockMvcRequestBuilders.put("/user/update/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"uid\": \"haha128\",\n" +
                                "    \"password\": \"12345678\",\n" +
                                "    \"phoneNumber\": \"13000000000\",\n" +
                                "    \"name\": \"dick\",\n" +
                                "    \"money\": 2000.0,\n" +
                                "    \"status\":\"离线\"\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("update user fail!"));
    }

    @Test
    void addUserCustomerSuccess() throws Exception {
        CustomerReq req = new CustomerReq(
                "haha128",
                -1,
                "123",
                "321",
                "1234567",
                0
        );

        Mockito.when(userService.addUserCustomer(Mockito.any())).thenReturn(1);

        mvc.perform(MockMvcRequestBuilders.post("/user/addCustomer")
                .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"uid\": \"haha128\",\n" +
                                "    \"password\": \"12345678\",\n" +
                                "    \"phoneNumber\": \"13000000000\",\n" +
                                "    \"name\": \"dick\",\n" +
                                "    \"money\": 2000.0,\n" +
                                "    \"status\":\"离线\"\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("add user customer success!"));
    }

    @Test
    void addUserCustomerFailed() throws Exception {
        CustomerReq req = new CustomerReq(
                "haha128",
                -1,
                "123",
                "321",
                "1234567",
                0
        );

        Mockito.when(userService.addUserCustomer(req)).thenReturn(0);

        mvc.perform(MockMvcRequestBuilders.post("/user/addCustomer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"uid\":\"haha128\",\n" +
                                "    \"cid\":-1,\n" +
                                "    \"cName\":\"123\",\n" +
                                "    \"cTel\":\"321\",\n" +
                                "    \"idCard\":\"1234567\",\n" +
                                "    \"cAge\":0\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("add user customer fail!"));
    }

    @Test
    void cancelUserCustomerSuccess() throws Exception {
        CustomerReq req = new CustomerReq(
                "haha128",
                -1,
                "123",
                "321",
                "1234567",
                0
        );

        Mockito.when(userService.deleteUserCustomer(Mockito.any())).thenReturn(1);

        mvc.perform(MockMvcRequestBuilders.post("/user/deleteCustomer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"uid\":\"haha128\",\n" +
                        "    \"cid\":-1,\n" +
                        "    \"cName\":\"123\",\n" +
                        "    \"cTel\":\"321\",\n" +
                        "    \"idCard\":\"1234567\",\n" +
                        "    \"cAge\":0\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("cancel user customer success!"));
    }

    @Test
    void cancelUserCustomerFailed() throws Exception {
        CustomerReq req = new CustomerReq(
                "haha128",
                -1,
                "123",
                "321",
                "1234567",
                0
        );
        User user = new User("haha128","dick","12345678",
                "13000000000",2000.0, "离线");

        Mockito.when(userService.deleteUserCustomer(req)).thenReturn(0);
        Mockito.when(userService.findUserByUid(req.getUid())).thenReturn(user);

        mvc.perform(MockMvcRequestBuilders.post("/user/deleteCustomer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"uid\":\"haha128\",\n" +
                                "    \"cid\":-1,\n" +
                                "    \"cName\":\"123\",\n" +
                                "    \"cTel\":\"321\",\n" +
                                "    \"idCard\":\"1234567\",\n" +
                                "    \"cAge\":0\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("cancel user customer fail!"));
    }
}