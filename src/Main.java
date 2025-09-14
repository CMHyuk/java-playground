import java.util.*;
import completableFuture.MathUtils;
import completableFuture.CompletableFutureExample;

public class Main {
    private static final int TASK_COUNT = 10000;
    
    public static void main(String[] args) {
        // 테스트 데이터 생성
        List<Integer> numbers = generateTestData();

        System.out.println("\n" + "=".repeat(60));
        
        // CompletableFuture 다양한 사용법 테스트
        CompletableFutureExample.basicSupplyAsync(new ArrayList<>(numbers));
        System.out.println("===================================================");
        CompletableFutureExample.thenApply(new ArrayList<>(numbers));
        System.out.println("===================================================");
        CompletableFutureExample.thenCompose(new ArrayList<>(numbers));
        System.out.println("===================================================");
        CompletableFutureExample.allOf(new ArrayList<>(numbers));
        System.out.println("===================================================");
        CompletableFutureExample.anyOf(new ArrayList<>(numbers));
        System.out.println("===================================================");
        CompletableFutureExample.handle(new ArrayList<>(numbers));
        System.out.println("===================================================");
        CompletableFutureExample.whenComplete(new ArrayList<>(numbers));
        System.out.println("===================================================");
        CompletableFutureExample.thenCombine(new ArrayList<>(numbers));
        System.out.println("===================================================");
    }

    private static List<Integer> generateTestData() {
        return MathUtils.generateTestData(TASK_COUNT, 1000);
    }
}