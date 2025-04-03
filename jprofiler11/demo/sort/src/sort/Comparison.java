package sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Comparison {

    private static final int DATA_COUNT = 100;
    private static List<Data> smallData = new ArrayList<Data>();
    private static List<Data> largeData = new ArrayList<Data>();

    public static void main(String[] args) {
        Random random = new Random(System.currentTimeMillis());
        generateData(50, smallData, random);
        generateData(2000, largeData, random);
        System.out.println("Warming up...");
        warmUp();
        System.out.println("Measuring...");
        measure();
        System.out.println("Done");
    }

    private static void generateData(int step, List<Data> data, Random random) {
        for (int i = 0; i < DATA_COUNT; i++) {
            int[] values = new int[step * (i + 1)];
            for (int j = 0; j < values.length; j++) {
                values[j] = random.nextInt();
            }
            data.add(new Data(values));
        }
    }

    private static void warmUp() {
        for (int i = 0; i < 2; i++) {
            System.out.println("Round " + (i + 1));
            execute();
        }
    }

    private static void measure() {
        execute();
    }

    private static void execute() {
        for (Data data : smallData) {
            executeBubbleSort(data.getCopy(), data.getSize());
        }
        for (Data data : smallData) {
            executeSelectionSort(data.getCopy(), data.getSize());
        }
        for (Data data : smallData) {
            executeInsertionSort(data.getCopy(), data.getSize());
        }
        for (Data data : largeData) {
            executeQuickSort(data.getCopy(), data.getSize());
        }
    }

    private static void executeBubbleSort(int[] values, int size /* used by the complexity script */) {
        Algorithms.bubbleSort(values);
    }

    private static void executeSelectionSort(int[] values, int size /* used by the complexity script */) {
        Algorithms.selectionSort(values);
    }

    private static void executeInsertionSort(int[] values, int size /* used by the complexity script */) {
        Algorithms.insertionSort(values);
    }

    private static void executeQuickSort(int[] values, int size /* used by the complexity script */) {
        Algorithms.quickSort(values);
    }

    private static class Data {
        int[] values;
        int[] copy;

        public Data(int[] values) {
            this.values = values;
            copy = new int[values.length];
        }

        public int[] getCopy() {
            System.arraycopy(values, 0, copy, 0, values.length);
            return copy;
        }

        public int getSize() {
            return values.length;
        }
    }
}
