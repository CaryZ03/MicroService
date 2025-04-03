package com.dofinal.RG.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dofinal.RG.entity.order.TrainCustomerOrder;
import com.dofinal.RG.entity.order.UserOrder;
import com.dofinal.RG.entity.train.Meal;
import com.dofinal.RG.entity.train.MealDemo;
import com.dofinal.RG.entity.train.Train;
import com.dofinal.RG.entity.train.TrainMeal;
import com.dofinal.RG.mapper.*;
import com.dofinal.RG.service.MealService;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MealServiceImpl extends ServiceImpl<MealMapper, Meal> implements MealService {
    @Autowired
    MealMapper mealMapper;
    @Autowired
    TrainMealMapper trainMealMapper;
    @Autowired
    TrainMapper trainMapper;

    @Override
    @Trace
    public Meal getMealByMid(int mid) {
        return mealMapper.getMealByMid(mid);
    }

    @Override
    @Trace
    public List<MealDemo> getMealsByTid(String tid) {
        if (tid == null) return null;
        List<TrainMeal> trainMeals = trainMealMapper.getTrainMealsByTid(tid);
        double price_rate = trainMapper.findTrainPriceRateByTid(tid);
        List<MealDemo> mealDemos = new ArrayList<>();
        for(TrainMeal trainMeal : trainMeals){
            Meal meal = mealMapper.getMealByMid(trainMeal.getMealId());
            MealDemo mealDemo = new MealDemo(tid, meal.getId(), meal.getName(), meal.getPrice() * price_rate,
                    trainMeal.getMealCount(), meal.getDetail(),meal.getType());
            mealDemos.add(mealDemo);
        }

        return mealDemos;
    }
    @Override
    @Trace
    public boolean buyMeal(String tid, int mid) {
        if (tid == null) return false;
        TrainMeal trainMeal = trainMealMapper.getTrainMealByTidAndMid(tid, mid);
        if (trainMealMapper.getTrainMealCountByTidAndMid(tid, mid) > 0) {
            trainMeal.buyMeal();
            trainMealMapper.updateMeal(trainMeal);
            return true;
        }
        return false;
    }

    @Override
    @Trace
    public boolean cancelMeal(String tid, int mid) {
        if (tid == null) return false;
        TrainMeal trainMeal = trainMealMapper.getTrainMealByTidAndMid(tid, mid);

        trainMeal.cancelMeal();
        trainMealMapper.updateMeal(trainMeal);
        return true;


    }
}
