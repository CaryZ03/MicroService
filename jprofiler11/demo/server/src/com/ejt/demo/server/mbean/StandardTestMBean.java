package com.ejt.demo.server.mbean;

public interface StandardTestMBean {
    Object getObjectValue();
    int getIntegerValue();
    void setIntegerValue(int integerValue);

    void operation();
    Object objectReturnOperation();
    int timesTwo(int value);
}
