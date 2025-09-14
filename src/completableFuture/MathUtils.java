package completableFuture;

import java.util.*;

public class MathUtils {
    
    /**
     * 소수 판별 (CPU 집약적인 작업)
     */
    public static boolean isPrime(int number) {
        if (number < 2) return false;
        if (number == 2) return true;
        if (number % 2 == 0) return false;
        
        for (int i = 3; i * i <= number; i += 2) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static List<Integer> generateTestData(int count, int maxValue) {
        List<Integer> numbers = new ArrayList<>();
        Random random = new Random();
        
        for (int i = 0; i < count; i++) {
            numbers.add(random.nextInt(maxValue) + 1);
        }
        
        return numbers;
    }

    public static class PerformanceTimer {

        private final long startTime;
        private final String operationName;
        
        public PerformanceTimer(String operationName) {
            this.operationName = operationName;
            this.startTime = System.currentTimeMillis();
        }
        
        public long getElapsedTime() {
            return System.currentTimeMillis() - startTime;
        }
        
        public void printResult(String result) {
            System.out.printf("%s 완료 - %s, 처리 시간: %d ms\n", operationName, result, getElapsedTime());
        }

    }

}
