package com.dofinal.RG.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dofinal.RG.entity.train.Meal;
import com.dofinal.RG.entity.train.MealDemo;
import org.apache.skywalking.apm.toolkit.trace.Trace;

import java.util.List;

public interface MealService extends IService<Meal> {
    @Trace
    Meal getMealByMid(int mid);

    @Trace
    List<MealDemo> getMealsByTid(String tid);
    @Trace
    boolean buyMeal(String tid, int mid);

    @Trace
    boolean cancelMeal(String tid, int mid);
}
