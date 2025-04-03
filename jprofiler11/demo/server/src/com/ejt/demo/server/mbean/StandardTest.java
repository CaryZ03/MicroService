package com.ejt.demo.server.mbean;

import java.math.BigDecimal;

public class StandardTest implements StandardTestMBean {

    private int integerValue = 0;

    @Override
    public Object getObjectValue() {
        return "1234";
    }

    @Override
    public int getIntegerValue() {
        return integerValue;
    }

    @Override
    public void setIntegerValue(int integerValue) {
        this.integerValue = integerValue;
    }

    @Override
    public void operation() {

    }

    @Override
    public Object objectReturnOperation() {
        return new BigDecimal(Math.PI);
    }

    @Override
    public int timesTwo(int value) {
        return 2 * value;
    }
}
