package com.dofinal.RG.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dofinal.RG.entity.train.Meal;
import com.dofinal.RG.entity.train.MealDemo;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface MealService extends IService<Meal> {
    Meal getMealByMid(int mid);

    List<MealDemo> getMealsByTid(String tid);
    boolean buyMeal(String tid, int mid);

    boolean cancelMeal(String tid, int mid);

    int getTrainMealCountByTidAndMid(String tid, int mid);

    double getMealPriceByMid(int mid);
}
