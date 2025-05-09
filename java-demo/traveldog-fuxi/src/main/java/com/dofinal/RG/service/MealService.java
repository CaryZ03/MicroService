package com.dofinal.RG.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dofinal.RG.entity.train.Meal;
import com.dofinal.RG.entity.train.MealDemo;
import java.util.List;
import org.apache.skywalking.apm.toolkit.trace.Trace;

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
