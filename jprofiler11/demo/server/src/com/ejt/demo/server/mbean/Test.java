package com.ejt.demo.server.mbean;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class Test implements TestMXBean {

    private ObjectName editableObjectName = createObjectName();

    private ObjectName createObjectName() {
        try {
            return new ObjectName("com.mycorp", "type", "controller");
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Date editableDate = new Date();
    private int editableInteger;
    private Integer editableIntegerWrapper;
    private long editableLong;
    private short editableShort;
    private float editableFloat;
    private double editableDouble;
    private char editableChar;
    private byte editableByte;
    private BigInteger editableBigInteger = new BigInteger("0");
    private BigDecimal editableBigDecimal = new BigDecimal("0");
    private boolean editableBoolean;
    private String editableString = "Hello";

    private int[] editableIntegerArray = new int[0];
    private long[] editableLongArray;
    private short[] editableShortArray;
    private float[] editableFloatArray;
    private double[] editableDoubleArray;
    private char[] editableCharArray;
    private BigInteger[] editableBigIntegerArray;
    private BigDecimal[] editableBigDecimalArray;
    private boolean[] editableBooleanArray;
    private String[] editableStringArray;
    private ObjectName[] editableObjectNameArray;

    private int counter;
    private TestData testData = new TestData();

    @Override
    public void doNothing() {

    }

    @Override
    public int incrementCounter() {
        return ++counter;
    }

    @Override
    public TestData[] showTestDatas(int count) {
        if (count < 0 || count > 20) {
            throw new IllegalArgumentException("Count must be between 0 and 20");
        }

        TestData[] testDatas = new TestData[count];
        for (int i = 0; i < count; i++) {
            testDatas[i] = new TestData();
        }

        return testDatas;
    }

    @Override
    public Date getCurrentDate() {
        return new Date();
    }

    @Override
    public TestData getTestData() {
        return testData;
    }

    @Override
    public ObjectName getEditableObjectName() {
        return editableObjectName;
    }

    @Override
    public void setEditableObjectName(ObjectName editableObjectName) {
        this.editableObjectName = editableObjectName;
    }

    @Override
    public Date getEditableDate() {
        return editableDate;
    }

    @Override
    public void setEditableDate(Date editableDate) {
        this.editableDate = editableDate;
    }

    @Override
    public int getEditableInteger() {
        return editableInteger;
    }

    @Override
    public void setEditableInteger(int editableInteger) {
        this.editableInteger = editableInteger;
    }

    @Override
    public Integer getEditableIntegerWrapper() {
        return editableIntegerWrapper;
    }

    @Override
    public void setEditableIntegerWrapper(Integer editableIntegerWrapper) {
        this.editableIntegerWrapper = editableIntegerWrapper;
    }

    @Override
    public long getEditableLong() {
        return editableLong;
    }

    @Override
    public void setEditableLong(long editableLong) {
        this.editableLong = editableLong;
    }

    @Override
    public short getEditableShort() {
        return editableShort;
    }

    @Override
    public void setEditableShort(short editableShort) {
        this.editableShort = editableShort;
    }

    @Override
    public float getEditableFloat() {
        return editableFloat;
    }

    @Override
    public void setEditableFloat(float editableFloat) {
        this.editableFloat = editableFloat;
    }

    @Override
    public double getEditableDouble() {
        return editableDouble;
    }

    @Override
    public void setEditableDouble(double editableDouble) {
        this.editableDouble = editableDouble;
    }

    @Override
    public char getEditableChar() {
        return editableChar;
    }

    @Override
    public void setEditableChar(char editableChar) {
        this.editableChar = editableChar;
    }

    @Override
    public byte getEditableByte() {
        return editableByte;
    }

    @Override
    public void setEditableByte(byte editableByte) {
        this.editableByte = editableByte;
    }

    @Override
    public BigInteger getEditableBigInteger() {
        return editableBigInteger;
    }

    @Override
    public void setEditableBigInteger(BigInteger editableBigInteger) {
        this.editableBigInteger = editableBigInteger;
    }

    @Override
    public BigDecimal getEditableBigDecimal() {
        return editableBigDecimal;
    }

    @Override
    public void setEditableBigDecimal(BigDecimal editableBigDecimal) {
        this.editableBigDecimal = editableBigDecimal;
    }

    @Override
    public boolean isEditableBoolean() {
        return editableBoolean;
    }

    @Override
    public void setEditableBoolean(boolean editableBoolean) {
        this.editableBoolean = editableBoolean;
    }

    @Override
    public String getEditableString() {
        return editableString;
    }

    @Override
    public void setEditableString(String editableString) {
        this.editableString = editableString;
    }

    @Override
    public int[] getEditableIntegerArray() {
        return editableIntegerArray;
    }

    @Override
    public void setEditableIntegerArray(int[] editableIntegerArray) {
        this.editableIntegerArray = editableIntegerArray;
    }

    @Override
    public long[] getEditableLongArray() {
        return editableLongArray;
    }

    @Override
    public void setEditableLongArray(long[] editableLongArray) {
        this.editableLongArray = editableLongArray;
    }

    @Override
    public short[] getEditableShortArray() {
        return editableShortArray;
    }

    @Override
    public void setEditableShortArray(short[] editableShortArray) {
        this.editableShortArray = editableShortArray;
    }

    @Override
    public float[] getEditableFloatArray() {
        return editableFloatArray;
    }

    @Override
    public void setEditableFloatArray(float[] editableFloatArray) {
        this.editableFloatArray = editableFloatArray;
    }

    @Override
    public double[] getEditableDoubleArray() {
        return editableDoubleArray;
    }

    @Override
    public void setEditableDoubleArray(double[] editableDoubleArray) {
        this.editableDoubleArray = editableDoubleArray;
    }

    @Override
    public char[] getEditableCharArray() {
        return editableCharArray;
    }

    @Override
    public void setEditableCharArray(char[] editableCharArray) {
        this.editableCharArray = editableCharArray;
    }

    @Override
    public BigInteger[] getEditableBigIntegerArray() {
        return editableBigIntegerArray;
    }

    @Override
    public void setEditableBigIntegerArray(BigInteger[] editableBigIntegerArray) {
        this.editableBigIntegerArray = editableBigIntegerArray;
    }

    @Override
    public BigDecimal[] getEditableBigDecimalArray() {
        return editableBigDecimalArray;
    }

    @Override
    public void setEditableBigDecimalArray(BigDecimal[] editableBigDecimalArray) {
        this.editableBigDecimalArray = editableBigDecimalArray;
    }

    @Override
    public boolean[] getEditableBooleanArray() {
        return editableBooleanArray;
    }

    @Override
    public void setEditableBooleanArray(boolean[] editableBooleanArray) {
        this.editableBooleanArray = editableBooleanArray;
    }

    @Override
    public String[] getEditableStringArray() {
        return editableStringArray;
    }

    @Override
    public void setEditableStringArray(String[] editableStringArray) {
        this.editableStringArray = editableStringArray;
    }

    @Override
    public ObjectName[] getEditableObjectNameArray() {
        return editableObjectNameArray;
    }

    @Override
    public void setEditableObjectNameArray(ObjectName[] editableObjectNameArray) {
        this.editableObjectNameArray = editableObjectNameArray;
    }
}
