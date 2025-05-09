package com.dofinal.RG.controller;

import com.dofinal.RG.entity.location.Location;
import com.dofinal.RG.entity.train.SeatDemo;
import com.dofinal.RG.entity.train.Train;
import com.dofinal.RG.service.SeatService;
import com.dofinal.RG.service.TrainService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * &#064;Classname TrainControllerTest
 * &#064;Description  TODO
 * &#064;Date 2024/8/20 18:51
 * &#064;Created MuJue
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@MockBean(ServerEndpointExporter.class)
class TrainControllerTest{
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TrainService trainService;
    @MockBean
    private SeatService seatService;

    @Test
    void getTrainsSuccess() throws Exception {
        List<Train> trains = new ArrayList<>();
        List<SeatDemo> seatDemos = new ArrayList<>();

        trains.add(new Train("haha128","绿皮"));
        trains.add(new Train("mujue37","普快"));
        trains.add(new Train("xixi255","特快"));

        seatDemos.add(new SeatDemo("软卧", 10, 20.0));
        seatDemos.add(new SeatDemo("硬座", 10, 20.0));
        seatDemos.add(new SeatDemo("站票", 10, 20.0));

        Mockito.when(trainService.findTrains()).thenReturn(trains);
        Mockito.when(seatService.getSeatDemoByTid(Mockito.any())).thenReturn(seatDemos);

        mockMvc.perform(MockMvcRequestBuilders.get("/train/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("get trains success!"));
    }

    @Test
    void getTrainsFailed() throws Exception{
        List<Train> trains = null;
        List<SeatDemo> seatDemos = new ArrayList<>();

        Mockito.when(trainService.findTrains()).thenReturn(trains);
        Mockito.when(seatService.getSeatDemoByTid(Mockito.any())).thenReturn(seatDemos);

        mockMvc.perform(MockMvcRequestBuilders.get("/train/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("get trains fail!"));
    }

    @Test
    void getTrainByTidSuccess() throws Exception {
        Train train = new Train("haha128","绿皮");
        List<SeatDemo> seatDemos = new ArrayList<>();

        seatDemos.add(new SeatDemo("软卧", 10, 20.0));
        seatDemos.add(new SeatDemo("硬座", 10, 20.0));
        seatDemos.add(new SeatDemo("站票", 10, 20.0));

        Mockito.when(trainService.findTrainByTid(train.getId())).thenReturn(train);
        Mockito.when(seatService.getSeatDemoByTid(train.getId())).thenReturn(seatDemos);

        mockMvc.perform(MockMvcRequestBuilders.get("/train/getByTid/" + train.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("get train by tid success!"));
    }

    @Test
    void getTrainByTidFailed() throws Exception {
        Train train = null;
        List<SeatDemo> seatDemos = new ArrayList<>();

        seatDemos.add(new SeatDemo("软卧", 10, 20.0));
        seatDemos.add(new SeatDemo("硬座", 10, 20.0));
        seatDemos.add(new SeatDemo("站票", 10, 20.0));

        Mockito.when(trainService.findTrainByTid(Mockito.any())).thenReturn(train);
        Mockito.when(seatService.getSeatDemoByTid(Mockito.any())).thenReturn(seatDemos);

        mockMvc.perform(MockMvcRequestBuilders.get("/train/getByTid/" + "dsadsa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("get train by tid fail!"));
    }

    @Test
    void getTidByUidSuccess() throws Exception{
        List<String> tids = new ArrayList<>();
        String uid = "dick";
        tids.add("haha128");
        tids.add("mujue37");
        tids.add("xixi255");

        Mockito.when(trainService.getTidByUid(uid)).thenReturn(tids);

        mockMvc.perform(MockMvcRequestBuilders.get("/train/getTidByUid/" + uid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("get tid by uid success!"));
    }

    @Test
    void getTidByUidFailed() throws Exception{
        List<String> tids = null;
        String uid = "dick";

        Mockito.when(trainService.getTidByUid(uid)).thenReturn(tids);

        mockMvc.perform(MockMvcRequestBuilders.get("/train/getTidByUid/" + uid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("get tid by uid fail!"));
    }

    @Test
    void getTrainByLocationsAndTimesSuccess() throws Exception{
        Location startLocation = new Location(1,"北京省","北京市");
        Location endLocation = new Location(2,"江苏省","南京市");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        List<Train> trains = new ArrayList<>();
        List<SeatDemo> seatDemos = new ArrayList<>();

        trains.add(new Train("haha128","绿皮"));
        trains.add(new Train("mujue37","普快"));
        trains.add(new Train("xixi255","特快"));

        seatDemos.add(new SeatDemo("软卧", 10, 20.0));
        seatDemos.add(new SeatDemo("硬座", 10, 20.0));
        seatDemos.add(new SeatDemo("站票", 10, 20.0));

        Mockito.when(trainService.findTrainByLocationAndTime(timestamp,startLocation,endLocation)).thenReturn(trains);
        Mockito.when(seatService.getSeatDemoByTidAndLocation(Mockito.any(String.class), Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(seatDemos);

        mockMvc.perform(MockMvcRequestBuilders.post("/train/getByTimeAndLocation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"uid\":\"haha128\",\n" +
                        "    \"curTime\":\"2024-06-11 12:00:00\",\n" +
                        "    \"startLocation\":{\n" +
                        "        \"id\":-1,\n" +
                        "        \"province\":\"北京省\",\n" +
                        "        \"city\":\"北京市\"\n" +
                        "    },\n" +
                        "    \"endLocation\":{\n" +
                        "        \"id\":-1,\n" +
                        "        \"province\":\"江苏省\",\n" +
                        "        \"city\":\"南京市\"\n" +
                        "    }\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("find train by time and location success!"));
    }

    @Test
    void getTrainByLocationsAndTimesFailed() throws Exception{
        Location startLocation = new Location(1,"北京省","北京市");
        Location endLocation = new Location(2,"江苏省","南京市");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        List<Train> trains = new ArrayList<>();
        List<SeatDemo> seatDemos = new ArrayList<>();

        trains.add(new Train("haha128","绿皮"));
        trains.add(new Train("mujue37","普快"));
        trains.add(new Train("xixi255","特快"));

        seatDemos.add(new SeatDemo("软卧", 10, 20.0));
        seatDemos.add(new SeatDemo("硬座", 10, 20.0));
        seatDemos.add(new SeatDemo("站票", 10, 20.0));

        Mockito.when(trainService.findTrainByLocationAndTime(timestamp,startLocation,endLocation)).thenReturn(trains);
        Mockito.when(seatService.getSeatDemoByTidAndLocation(Mockito.any(String.class), Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(seatDemos);

        mockMvc.perform(MockMvcRequestBuilders.post("/train/getByTimeAndLocation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"uid\":\"haha128\",\n" +
                                "    \"curTime\":\"2024-06-11 12:00:00\",\n" +
                                "    \"startLocation\": null," +
                                "    \"endLocation\":{\n" +
                                "        \"id\":-1,\n" +
                                "        \"province\":\"江苏省\",\n" +
                                "        \"city\":\"南京市\"\n" +
                                "    }\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("the number of location is wrong!"));
        mockMvc.perform(MockMvcRequestBuilders.post("/train/getByTimeAndLocation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"uid\":\"haha128\",\n" +
                                "    \"curTime\":\"2024-06-11 12:00:00\",\n" +
                                "    \"startLocation\":{\n" +
                                "        \"id\":-1,\n" +
                                "        \"province\":\"北京省\",\n" +
                                "        \"city\":\"北京市\"\n" +
                                "    },\n" +
                                "    \"endLocation\":null" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("the number of location is wrong!"));

        Mockito.when(trainService.findTrainByLocationAndTime(Mockito.any(Timestamp.class),Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.post("/train/getByTimeAndLocation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"uid\":\"haha128\",\n" +
                                "    \"curTime\":\"2024-06-11 12:00:00\",\n" +
                                "    \"startLocation\":{\n" +
                                "        \"id\":-1,\n" +
                                "        \"province\":\"北京省\",\n" +
                                "        \"city\":\"北京市\"\n" +
                                "    },\n" +
                                "    \"endLocation\":{\n" +
                                "        \"id\":-1,\n" +
                                "        \"province\":\"江苏省\",\n" +
                                "        \"city\":\"南京市\"\n" +
                                "    }\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("find train by time and location fail!"));
    }



    @Test
    void updateTrainSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/train/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"uid\":\"haha128\",\n" +
                        "    \"curTime\":\"2024-06-11 12:00:00\",\n" +
                        "    \"startLocation\":{\n" +
                        "        \"id\":-1,\n" +
                        "        \"province\":\"北京省\",\n" +
                        "        \"city\":\"北京市\"\n" +
                        "    },\n" +
                        "    \"endLocation\":{\n" +
                        "        \"id\":-1,\n" +
                        "        \"province\":\"江苏省\",\n" +
                        "        \"city\":\"南京市\"\n" +
                        "    }\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("update train success!"));
    }

    @Test
    void updateTrainFailed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/train/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"uid\":null,\n" +
                                "    \"curTime\":\"2024-06-11 12:00:00\",\n" +
                                "    \"startLocation\":{\n" +
                                "        \"id\":-1,\n" +
                                "        \"province\":\"北京省\",\n" +
                                "        \"city\":\"北京市\"\n" +
                                "    },\n" +
                                "    \"endLocation\":{\n" +
                                "        \"id\":-1,\n" +
                                "        \"province\":\"江苏省\",\n" +
                                "        \"city\":\"南京市\"\n" +
                                "    }\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("update train fail!"));

    }
    @Test
    void deleteTrainByTidSuccess() throws Exception{
        Integer tid = 1;
        mockMvc.perform(MockMvcRequestBuilders.delete("/train/deleteByTid/" + tid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("delete train success!"));
    }

    @Test
    void deleteTrainByTidFailed() throws Exception{
        Integer tid = -1;
        mockMvc.perform(MockMvcRequestBuilders.delete("/train/deleteByTid/" + tid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("delete train fail!"));
    }
}