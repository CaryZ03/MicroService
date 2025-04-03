package com.ejt.demo.server.mbean;

import java.beans.ConstructorProperties;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestData {

    private Map<String, int[]> intArrayMap = new LinkedHashMap<>();
    private Map<String, NestedTestData> nestedDataMap = new LinkedHashMap<>();
    private Map<String, BigInteger> bigIntegerMap = new LinkedHashMap<>();
    private Map<String, Integer> pathMap = new LinkedHashMap<>();
    private int value = 42;
    private String message = "Hello world!";

    public TestData() {
        intArrayMap.put("one", new int[] {1, 2, 3});
        intArrayMap.put("two", new int[] {2, 3, 4});
        intArrayMap.put("three", new int[] {3, 4, 5});

        nestedDataMap.put("one", new NestedTestData());
        nestedDataMap.put("two", new NestedTestData());
        nestedDataMap.put("three", new NestedTestData());

        bigIntegerMap.put("one", new BigInteger("1234567"));
        bigIntegerMap.put("two", new BigInteger("1375475751111111"));
        bigIntegerMap.put("three", new BigInteger("3541364524255255638615632"));

        pathMap.put("c:\\windows\\path", 10);
        pathMap.put("/unix/path", 20);
    }

    @ConstructorProperties({"intArrayMap", "nestedDataMap", "bigIntegerMap", "value", "message"})
    public TestData(Map<String, int[]> intArrayMap, Map<String, NestedTestData> nestedDataMap, Map<String, BigInteger> bigIntegerMap, int value, String message) {
        this.intArrayMap = intArrayMap;
        this.nestedDataMap = nestedDataMap;
        this.bigIntegerMap = bigIntegerMap;
        this.value = value;
        this.message = message;
    }

    public Map<String, int[]> getIntArrayMap() {
        return intArrayMap;
    }

    public Map<String, NestedTestData> getNestedDataMap() {
        return nestedDataMap;
    }

    public Map<String, BigInteger> getBigIntegerMap() {
        return bigIntegerMap;
    }

    public Map<String, Integer> getPathMap() {
        return pathMap;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }
}
