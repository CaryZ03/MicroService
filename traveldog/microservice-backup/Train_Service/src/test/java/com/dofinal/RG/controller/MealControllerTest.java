package com.dofinal.RG.controller;

import com.dofinal.RG.entity.train.Meal;
import com.dofinal.RG.entity.train.MealDemo;
import com.dofinal.RG.service.MealService;
import com.dofinal.RG.reqs.entity.MealReq;
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

import java.util.Collections;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@MockBean(ServerEndpointExporter.class)
class MealControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MealService mealService;
    @MockBean
    private NoticeUtil noticeUtil;

    @Test
        public void GetTrainMealsSuccess() throws Exception {
            MealReq mealReq = new MealReq();
            mealReq.setTid("A01");
            MealDemo mealDemo = new MealDemo("A01",2,"shandong",333.3,1,"sda","kuaiche");

        Mockito.when(mealService.getMealsByTid("A01")).thenReturn(Collections.singletonList(mealDemo));

            mockMvc.perform(MockMvcRequestBuilders.post("/meal/getTrainMeals")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"tid\": \"A01\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("get train mealDemos success"))
                    .andExpect(jsonPath("$.content[0]").exists());
        }
        @Test
         public void GetTrainMealsFail() throws Exception {
        MealReq mealReq = new MealReq();
        mealReq.setTid("1");
        Mockito.when(mealService.getMealsByTid("1")).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.post("/meal/getTrainMeals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tid\": 1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("get train mealDemos failed"))
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void buyTrainMealSuccess() throws Exception{
        MealReq mealReq = new MealReq();
        mealReq.setTid("1");
        mealReq.setMid(101);
        mealReq.setUid("user123");

        Mockito.when(mealService.buyMeal("1", 101)).thenReturn(true); // buyMeal 返回 true
        Meal meal = new Meal(1,"tofu","sd",5.5,"da");
        Mockito.when(mealService.getMealByMid(101)).thenReturn(meal); // getMealByMid 返回指定的 Meal 对象

        mockMvc.perform(MockMvcRequestBuilders.post("/meal/buyTrainMeal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tid\": \"1\", \"mid\": 101, \"uid\": \"user123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("buy meal success"))
                .andExpect(jsonPath("$.content").value(true));
        Mockito.verify(noticeUtil).addNotice("user123", "您已成功预定1火车餐，菜品名称为tofu");
    }
    @Test
    void buyTrainMealFail() throws Exception{
            MealReq mealReq = new MealReq();
            mealReq.setTid("1");
            mealReq.setMid(101);
            mealReq.setUid("user123");
            Mockito.when(mealService.buyMeal("1", 101)).thenReturn(false); // buyMeal 返回 false
            mockMvc.perform(MockMvcRequestBuilders.post("/meal/buyTrainMeal")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"tid\": \"1\", \"mid\": 101, \"uid\": \"user123\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("buy meal failed"))
                    .andExpect(jsonPath("$.content").value(false));

            // 验证 addNotice 被调用的参数
            Mockito.verify(noticeUtil).addNotice("user123", "1火车的火车餐预定失败，请重新预定。");
        }

    @Test
    void cancelTrainMealSuccess() throws Exception{
        MealReq mealReq = new MealReq();
        mealReq.setTid("1");
        mealReq.setMid(101);
        mealReq.setUid("user123");
        // 设置 mealService 的 mock 行为
        Mockito.when(mealService.cancelMeal("1", 101)).thenReturn(true); // cancelMeal 返回 true
        mockMvc.perform(MockMvcRequestBuilders.post("/meal/cancelTrainMeal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tid\": \"1\", \"mid\": 101, \"uid\": \"user123\"}"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("cancel meal success"))
                .andExpect(jsonPath("$.content").value(true));
        Mockito.verify(noticeUtil).addNotice("user123", "您预定的1火车的火车餐订单取消成功！");
    }
    @Test
    void cancelTrainMealFail() throws Exception{
        MealReq mealReq = new MealReq();
        mealReq.setTid("1");
        mealReq.setMid(101);
        mealReq.setUid("user123");

        // 设置 mealService 的 mock 行为，返回 false 表示取消失败
        Mockito.when(mealService.cancelMeal("1", 101)).thenReturn(false);

        // 执行请求
        mockMvc.perform(MockMvcRequestBuilders.post("/meal/cancelTrainMeal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tid\": \"1\", \"mid\": 101, \"uid\": \"user123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("cancel meal failed"))
                .andExpect(jsonPath("$.content").value(false));

        // 验证 addNotice 被调用的参数
        Mockito.verify(noticeUtil).addNotice("user123", "您预定的1火车的火车餐订单取消失败，请重新操作！");
    }
}









