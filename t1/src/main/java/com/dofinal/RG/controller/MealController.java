package com.dofinal.RG.controller;

import com.dofinal.RG.entity.train.Meal;
import com.dofinal.RG.entity.train.MealDemo;
import com.dofinal.RG.reqs.entity.MealReq;
import com.dofinal.RG.rsps.BaseRsp;
import com.dofinal.RG.service.MealService;
import com.dofinal.RG.util.NoticeUtil;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
public class MealController {
    @Autowired
    MealService mealService;

    @Autowired
    private NoticeUtil noticeUtil;

    @PostMapping("/meal/getTrainMeals")
    @Trace
    public BaseRsp<List<MealDemo>> getTrainMeals(@RequestBody MealReq req) {
        List<MealDemo> mealDemos = mealService.getMealsByTid(req.getTid());
        BaseRsp<List<MealDemo>> rsp = new BaseRsp<>();
        if (mealDemos != null) {
            rsp.setSuccess(true);
            rsp.setMessage("get train mealDemos success");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("get train mealDemos failed");
        }
        rsp.setContent(mealDemos);
        return rsp;
    }

    @PostMapping("/meal/buyTrainMeal")
    @Trace
    public BaseRsp<Boolean> BuyMeal(@RequestBody MealReq req) {
        boolean result = mealService.buyMeal(req.getTid(), req.getMid());
        BaseRsp<Boolean> rsp = new BaseRsp<>();
        if (result) {
            rsp.setSuccess(true);
            rsp.setMessage("buy meal success");
            Meal meal = mealService.getMealByMid(req.getMid());
            String content = "您已成功预定" + req.getTid() + "火车餐，菜品名称为" + meal.getName();
            noticeUtil.addNotice(req.getUid(), content);
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("buy meal failed");
            String content = req.getTid() + "火车的火车餐预定失败，请重新预定。";
            noticeUtil.addNotice(req.getUid(), content);
        }
        rsp.setContent(result);
        return rsp;

    }

    @PostMapping("/meal/cancelTrainMeal")
    @Trace
    public BaseRsp<Boolean> CancelMeal(@RequestBody MealReq req) {
        boolean result = mealService.cancelMeal(req.getTid(), req.getMid());
        BaseRsp<Boolean> rsp = new BaseRsp<>();
        if (result) {
            rsp.setSuccess(true);
            rsp.setMessage("cancel meal success");
            String content = "您预定的" + req.getTid() + "火车的火车餐订单取消成功！";
            noticeUtil.addNotice(req.getUid(), content);
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("cancel meal failed");
            String content = "您预定的" + req.getTid() + "火车的火车餐订单取消失败，请重新操作！";
            noticeUtil.addNotice(req.getUid(), content);
        }
        rsp.setContent(result);
        return rsp;
    }

}
