package com.dofinal.RG.entity.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Timestamp;


@TableName("meal_orders")
public class MealCustomerOrder extends CustomerOrder {
    @TableField("m_id")
    private int mealId;
    @TableField("t_id")
    private String trainId;
    @TableField("mo_price")
    private double price;
    public MealCustomerOrder(int orderId, int customerId,  int mealId, String trainId, Timestamp addTime,double price) {
        super(orderId, customerId, addTime);
        this.mealId = mealId;
        this.trainId = trainId;
        this.price = price;
    }

    @Override
    public String toString() {
        return "MealCustomerOrder{" +
                "mealId=" + mealId +
                ", oid=" + orderId +
                ", cid=" + customerId +
                '}';
    }

    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
