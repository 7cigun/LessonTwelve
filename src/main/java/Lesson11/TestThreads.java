package Lesson11;

import java.util.Arrays;

public class TestThreads {

    static final int SIZE = 10000000;
    static final int HALF = SIZE / 2;
    private static final Object mon = new Object();

    public static void main(String[] args) {
        System.out.println("Время выполнения метода без потоков = " + methodWithoutThreads());
        System.out.println("Время выполнения метода c двумя потоками = " + methodWithThreads());

    }

    private static long methodWithoutThreads() {
        float[] arr = new float[SIZE];
        Arrays.fill(arr, 1);

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < SIZE; i++) {
            arr[i] = (float) (arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
        }
        long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }

    private static long methodWithThreads() {
        float[] arr = new float[SIZE];
        float[] arr1 = new float[HALF];
        float[] arr2 = new float[HALF];

        Arrays.fill(arr, 1);

        long startTime = System.currentTimeMillis();

        System.arraycopy(arr, 0, arr1, 0, HALF);
        System.arraycopy(arr, HALF, arr2, 0, HALF);

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < HALF; i++) {
                    arr1[i] = (float) (arr1[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
                }
                synchronized (mon) {
                    System.arraycopy(arr1, 0, arr, 0, HALF);
                }
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < HALF; i++) {
                arr2[i] = (float) (arr2[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
            }
            synchronized (mon) {
                System.arraycopy(arr2, 0, arr, HALF, HALF);
            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }
}
