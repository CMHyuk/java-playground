package completableFuture;

import java.util.*;
import java.util.concurrent.*;

public class CompletableFutureExample {
    
    private static final int CPU_CORES = Runtime.getRuntime().availableProcessors();

    /**
     * 기본 supplyAsync - 단일 작업
     * 사용법: 비동기 작업을 시작하고 결과를 기다림
     * 언제 사용: 단순한 비동기 작업이 필요할 때
     */
    public static void basicSupplyAsync(List<Integer> numbers) {
        System.out.println("기본 supplyAsync");
        System.out.println("   → 비동기 작업을 시작하고 결과를 기다림");
        System.out.println("   → 언제 사용: 단순한 비동기 작업이 필요할 때");
        
        MathUtils.PerformanceTimer timer = new MathUtils.PerformanceTimer("supplyAsync");
        
        CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> 
            numbers.stream()
                .mapToLong(number -> MathUtils.isPrime(number) ? 1 : 0)
                .sum()
        );
        
        long result = future.join();
        timer.printResult("소수 개수: " + result);
    }
    
    /**
     * thenApply - 결과 변환
     * 사용법: 이전 작업 결과를 받아서 동기적으로 변환
     * 언제 사용: 결과를 가공하거나 변환할 때
     */
    public static void thenApply(List<Integer> numbers) {
        System.out.println("thenApply 체이닝");
        System.out.println("   → 이전 작업 결과를 받아서 동기적으로 변환");
        System.out.println("   → 언제 사용: 결과를 가공하거나 변환할 때");
        
        MathUtils.PerformanceTimer timer = new MathUtils.PerformanceTimer("thenApply");
        
        CompletableFuture<Long> future = CompletableFuture
            .supplyAsync(() -> numbers.stream().mapToLong(n -> n).sum())
            .thenApply(sum -> sum * sum); // 제곱 계산
        
        long result = future.join();
        timer.printResult("합의 제곱: " + result);
    }
    
    /**
     * thenCompose - 비동기 체이닝
     * 사용법: 이전 작업 결과를 받아서 새로운 비동기 작업 실행
     * 언제 사용: 연속적인 비동기 작업이 필요할 때
     */
    public static void thenCompose(List<Integer> numbers) {
        System.out.println("thenCompose 체이닝");
        System.out.println("   → 이전 작업 결과를 받아서 새로운 비동기 작업 실행");
        System.out.println("   → 언제 사용: 연속적인 비동기 작업이 필요할 때");
        
        MathUtils.PerformanceTimer timer = new MathUtils.PerformanceTimer("thenCompose");
        
        CompletableFuture<Long> future = CompletableFuture
            .supplyAsync(() -> numbers.stream().filter(MathUtils::isPrime).count())
            .thenCompose(count -> CompletableFuture.supplyAsync(() -> count * count));
        
        long result = future.join();
        timer.printResult("소수 개수의 제곱: " + result);
    }
    
    /**
     * allOf - 모든 작업 완료 대기
     * 사용법: 여러 작업을 병렬로 실행하고 모든 작업이 완료될 때까지 대기
     * 언제 사용: 여러 작업의 결과를 모두 모아야 할 때
     */
    public static void allOf(List<Integer> numbers) {
        System.out.println("allOf - 모든 작업 완료");
        System.out.println("   → 여러 작업을 병렬로 실행하고 모든 작업이 완료될 때까지 대기");
        System.out.println("   → 언제 사용: 여러 작업의 결과를 모두 모아야 할 때");
        
        MathUtils.PerformanceTimer timer = new MathUtils.PerformanceTimer("allOf");
        
        List<List<Integer>> chunks = splitIntoChunks(numbers);
        
        List<CompletableFuture<Long>> futures = chunks.stream()
            .map(chunk -> CompletableFuture.supplyAsync(() -> 
                chunk.stream().mapToLong(n -> MathUtils.isPrime(n) ? 1 : 0).sum()))
            .toList();
        
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );
        
        allFutures.join();
        
        long total = futures.stream().mapToLong(CompletableFuture::join).sum();
        timer.printResult("총 소수 개수: " + total);
    }
    
    /**
     * anyOf - 첫 번째 완료된 작업
     * 사용법: 여러 작업 중 가장 먼저 완료되는 작업의 결과를 반환
     * 언제 사용: 여러 방법 중 하나만 성공하면 될 때 (경쟁 조건)
     */
    public static void anyOf(List<Integer> numbers) {
        System.out.println("anyOf - 첫 번째 완료");
        System.out.println("   → 여러 작업 중 가장 먼저 완료되는 작업의 결과를 반환");
        System.out.println("   → 언제 사용: 여러 방법 중 하나만 성공하면 될 때 (경쟁 조건)");
        
        MathUtils.PerformanceTimer timer = new MathUtils.PerformanceTimer("anyOf");
        
        CompletableFuture<Long> future1 = CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            return numbers.stream().mapToLong(n -> n).sum();
        });
        
        CompletableFuture<Long> future2 = CompletableFuture.supplyAsync(() -> 
            numbers.stream().mapToLong(n -> n * n).sum()
        );
        
        CompletableFuture<Object> anyFuture = CompletableFuture.anyOf(future1, future2);
        
        Object result = anyFuture.join();
        timer.printResult("첫 번째 완료 결과: " + result);
    }
    
    /**
     * handle - 예외 처리
     * 사용법: 성공/실패 여부와 관계없이 결과를 처리
     * 언제 사용: 예외가 발생해도 계속 진행해야 할 때
     */
    public static void handle(List<Integer> numbers) {
        System.out.println("handle - 예외 처리");
        System.out.println("   → 성공/실패 여부와 관계없이 결과를 처리");
        System.out.println("   → 언제 사용: 예외가 발생해도 계속 진행해야 할 때");
        
        MathUtils.PerformanceTimer timer = new MathUtils.PerformanceTimer("handle");
        
        CompletableFuture<Long> future = CompletableFuture
            .supplyAsync(() -> {
                if (numbers.isEmpty()) {
                    throw new RuntimeException("빈 리스트");
                }
                return numbers.stream().mapToLong(n -> n).sum();
            })
            .handle((result, throwable) -> {
                if (throwable != null) {
                    System.out.println("예외 발생: " + throwable.getMessage());
                    return 0L;
                }
                return result;
            });
        
        long result = future.join();
        timer.printResult("결과: " + result);
    }
    
    /**
     * whenComplete - 완료 시 콜백
     * 사용법: 작업 완료 시 성공/실패 여부와 관계없이 콜백 실행
     * 언제 사용: 로깅, 리소스 정리, 통계 수집 등이 필요할 때
     */
    public static void whenComplete(List<Integer> numbers) {
        System.out.println("whenComplete - 완료 콜백");
        System.out.println("   → 작업 완료 시 성공/실패 여부와 관계없이 콜백 실행");
        System.out.println("   → 언제 사용: 로깅, 리소스 정리, 통계 수집 등이 필요할 때");
        
        MathUtils.PerformanceTimer timer = new MathUtils.PerformanceTimer("whenComplete");
        
        CompletableFuture<Long> future = CompletableFuture
            .supplyAsync(() -> numbers.stream().mapToLong(n -> n).sum())
            .whenComplete((result, throwable) -> {
                if (throwable == null) {
                    System.out.println("작업 완료: " + result);
                } else {
                    System.out.println("작업 실패: " + throwable.getMessage());
                }
            });
        
        long result = future.join();
        timer.printResult("최종 결과: " + result);
    }
    
    /**
     * thenCombine - 두 작업 결과 결합
     * 사용법: 두 개의 독립적인 작업 결과를 결합하여 새로운 결과 생성
     * 언제 사용: 여러 작업의 결과를 조합해야 할 때
     */
    public static void thenCombine(List<Integer> numbers) {
        System.out.println("thenCombine - 두 작업 결합");
        System.out.println("   → 두 개의 독립적인 작업 결과를 결합하여 새로운 결과 생성");
        System.out.println("   → 언제 사용: 여러 작업의 결과를 조합해야 할 때");
        
        MathUtils.PerformanceTimer timer = new MathUtils.PerformanceTimer("thenCombine");
        
        CompletableFuture<Long> sumFuture = CompletableFuture.supplyAsync(() -> 
            numbers.stream().mapToLong(n -> n).sum()
        );
        
        CompletableFuture<Long> countFuture = CompletableFuture.supplyAsync(() -> 
            (long) numbers.size()
        );
        
        CompletableFuture<Double> combinedFuture = sumFuture.thenCombine(countFuture, 
            (sum, count) -> (double) sum / count
        );
        
        double result = combinedFuture.join();
        timer.printResult("평균: " + result);
    }
    
    private static List<List<Integer>> splitIntoChunks(List<Integer> list) {
        List<List<Integer>> chunks = new ArrayList<>();
        int chunkSize = list.size() / CompletableFutureExample.CPU_CORES;
        
        for (int i = 0; i < CompletableFutureExample.CPU_CORES; i++) {
            int start = i * chunkSize;
            int end = (i == CompletableFutureExample.CPU_CORES - 1) ? list.size() : (i + 1) * chunkSize;
            chunks.add(list.subList(start, end));
        }
        
        return chunks;
    }

}