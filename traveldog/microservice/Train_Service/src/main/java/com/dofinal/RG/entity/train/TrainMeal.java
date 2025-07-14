package com.dofinal.RG.entity.train;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * &#064;Classname TrainMeal
 * &#064;Description  TODO
 * &#064;Date 2024/5/19 20:28
 * &#064;Created MuJue
 */
@TableName("train_meal")
public class TrainMeal {

    @TableField("t_id")
    private String trainId;
    @TableField("m_id")
    private int mealId;
    @TableField("m_count")
    private int mealCount;

    public TrainMeal(String trainId, int mealId, int mealCount) {
        this.trainId = trainId;
        this.mealId = mealId;
        this.mealCount = mealCount;
    }

    @Override
    public String toString() {
        return "TrainMeal{" +
                "t_id='" + trainId + '\'' +
                ", mealId=" + mealId +
                ", mealCount=" + mealCount +
                '}';
    }

    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

    public int getMealCount() {
        return mealCount;
    }

    public void setMealCount(int mealCount) {
        this.mealCount = mealCount;
    }

    public void buyMeal() {
        this.mealCount -= 1;
    }

    public void cancelMeal() {
        this.mealCount += 1;
    }
}
