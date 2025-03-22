package com.ejt.demo.server.mbean;

import javax.management.ObjectName;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public interface TestMXBean {

    void doNothing();
    int incrementCounter();
    TestData[] showTestDatas(int count);

    Date getCurrentDate();
    TestData getTestData();

    int getEditableInteger();
    void setEditableInteger(int value);

    Integer getEditableIntegerWrapper();
    void setEditableIntegerWrapper(Integer editableIntegerWrapper);

    long getEditableLong();
    void setEditableLong(long value);

    short getEditableShort();
    void setEditableShort(short value);

    float getEditableFloat();
    void setEditableFloat(float value);

    double getEditableDouble();
    void setEditableDouble(double value);

    char getEditableChar();
    void setEditableChar(char value);

    byte getEditableByte();
    void setEditableByte(byte editableByte);

    BigInteger getEditableBigInteger();
    void setEditableBigInteger(BigInteger value);

    BigDecimal getEditableBigDecimal();
    void setEditableBigDecimal(BigDecimal value);

    boolean isEditableBoolean();
    void setEditableBoolean(boolean value);

    ObjectName getEditableObjectName();
    void setEditableObjectName(ObjectName editableObjectName);

    Date getEditableDate();
    void setEditableDate(Date date);

    String getEditableString();
    void setEditableString(String editableString);

    int[] getEditableIntegerArray();
    void setEditableIntegerArray(int[] editableIntegerArray);

    long[] getEditableLongArray();
    void setEditableLongArray(long[] editableLongArray);

    short[] getEditableShortArray();
    void setEditableShortArray(short[] editableShortArray);

    float[] getEditableFloatArray();
    void setEditableFloatArray(float[] editableFloatArray);

    double[] getEditableDoubleArray();
    void setEditableDoubleArray(double[] editableDoubleArray);

    char[] getEditableCharArray();
    void setEditableCharArray(char[] editableCharArray);

    BigInteger[] getEditableBigIntegerArray();
    void setEditableBigIntegerArray(BigInteger[] editableBigIntegerArray);

    BigDecimal[] getEditableBigDecimalArray();
    void setEditableBigDecimalArray(BigDecimal[] editableBigDecimalArray);

    boolean[] getEditableBooleanArray();
    void setEditableBooleanArray(boolean[] editableBooleanArray);

    String[] getEditableStringArray();
    void setEditableStringArray(String[] editableStringArray);

    ObjectName[] getEditableObjectNameArray();

    void setEditableObjectNameArray(ObjectName[] editableObjectNameArray);
}
