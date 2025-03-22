package sort;

public class Algorithms {

    public static void bubbleSort(int[] values) {
        for (int i = 0; i < values.length; i++) {
            for (int j = 1; j < values.length; j++) {
                if (values[j - 1] > values[j]) {
                    int temp = values[j];
                    values[j] = values[j - 1];
                    values[j - 1] = temp;
                }
            }
        }
    }

    public static void selectionSort(int[] values) {
        for (int i = values.length - 1; i > 0; i--) {
            int highest = values[0];
            int index = 0;
            for (int j = 0; j < i; j++) {
                if (values[j] > highest) {
                    highest = values[j];
                    index = j;
                }
            }
            int temp = values[i - 1] = highest;
            values[index] = temp;
        }
    }

    public static void insertionSort(int[] values) {
        for (int i = 1; i < values.length; i++) {
            int index = values[i];
            int j = i;
            while ((j > 0) && (values[j - 1] > index)) {
                values[j] = values[j - 1];
                j = j - 1;
            }
            values[j] = index;
        }
    }

    public static void quickSort(int[] values) {
        quickSort(values, 0, values.length - 1);
    }

    private static void quickSort(int[] values, int low, int high) {
        int i = low, j = high;
        int pivot = values[low + (high - low) / 2];

        while (i <= j) {
            while (values[i] < pivot) {
                i++;
            }
            while (values[j] > pivot) {
                j--;
            }
            if (i <= j) {
                int temp = values[i];
                values[i] = values[j];
                values[j] = temp;
                i++;
                j--;
            }
        }
        if (low < j) {
            quickSort(values, low, j);
        }
        if (i < high) {
            quickSort(values, i, high);
        }
    }

}