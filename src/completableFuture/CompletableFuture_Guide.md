# CompletableFuture 사용법 가이드

## 개요

CompletableFuture는 Java 8에서 도입된 비동기 프로그래밍을 위한 클래스입니다. Future의 한계를 극복하고 비동기 작업을 더 쉽게 처리할 수 있게 해줍니다.

## 주요 메서드별 사용법

### 1. supplyAsync - 기본 비동기 작업

**사용법**: 비동기 작업을 시작하고 결과를 기다림  
**언제 사용**: 단순한 비동기 작업이 필요할 때

```java
CompletableFuture<Long> future = CompletableFuture.supplyAsync(() ->
        numbers.stream()
                .mapToLong(number -> MathUtils.isPrime(number) ? 1 : 0)
                .sum()
);
long result = future.join();
```

### 2. thenApply - 결과 변환

**사용법**: 이전 작업 결과를 받아서 동기적으로 변환  
**언제 사용**: 결과를 가공하거나 변환할 때

```java
CompletableFuture<Long> future = CompletableFuture
        .supplyAsync(() -> numbers.stream().mapToLong(n -> n).sum())
        .thenApply(sum -> sum * sum); // 제곱 계산
```

### 3. thenCompose - 비동기 체이닝

**사용법**: 이전 작업 결과를 받아서 새로운 비동기 작업 실행  
**언제 사용**: 연속적인 비동기 작업이 필요할 때

```java
CompletableFuture<Long> future = CompletableFuture
        .supplyAsync(() -> numbers.stream().filter(MathUtils::isPrime).count())
        .thenCompose(count -> CompletableFuture.supplyAsync(() -> count * count));
```

### 4. allOf - 모든 작업 완료 대기

**사용법**: 여러 작업을 병렬로 실행하고 모든 작업이 완료될 때까지 대기  
**언제 사용**: 여러 작업의 결과를 모두 모아야 할 때

```java
List<CompletableFuture<Long>> futures = chunks.stream()
        .map(chunk -> CompletableFuture.supplyAsync(() ->
                chunk.stream().mapToLong(n -> MathUtils.isPrime(n) ? 1 : 0).sum()))
        .toList();

CompletableFuture<Void> allFutures = CompletableFuture.allOf(
        futures.toArray(new CompletableFuture[0])
);
allFutures.

join();
```

### 5. anyOf - 첫 번째 완료된 작업

**사용법**: 여러 작업 중 가장 먼저 완료되는 작업의 결과를 반환  
**언제 사용**: 여러 방법 중 하나만 성공하면 될 때 (경쟁 조건)

```java
CompletableFuture<Long> future1 = CompletableFuture.supplyAsync(() -> {
    Thread.sleep(100);
    return numbers.stream().mapToLong(n -> n).sum();
});

CompletableFuture<Long> future2 = CompletableFuture.supplyAsync(() ->
        numbers.stream().mapToLong(n -> n * n).sum()
);

CompletableFuture<Object> anyFuture = CompletableFuture.anyOf(future1, future2);
Object result = anyFuture.join();
```

### 6. handle - 예외 처리

**사용법**: 성공/실패 여부와 관계없이 결과를 처리  
**언제 사용**: 예외가 발생해도 계속 진행해야 할 때

```java
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
```

### 7. whenComplete - 완료 시 콜백

**사용법**: 작업 완료 시 성공/실패 여부와 관계없이 콜백 실행  
**언제 사용**: 로깅, 리소스 정리, 통계 수집 등이 필요할 때

```java
CompletableFuture<Long> future = CompletableFuture
        .supplyAsync(() -> numbers.stream().mapToLong(n -> n).sum())
        .whenComplete((result, throwable) -> {
            if (throwable == null) {
                System.out.println("작업 완료: " + result);
            } else {
                System.out.println("작업 실패: " + throwable.getMessage());
            }
        });
```

### 8. thenCombine - 두 작업 결과 결합

**사용법**: 두 개의 독립적인 작업 결과를 결합하여 새로운 결과 생성  
**언제 사용**: 여러 작업의 결과를 조합해야 할 때

```java
CompletableFuture<Long> sumFuture = CompletableFuture.supplyAsync(() ->
        numbers.stream().mapToLong(n -> n).sum()
);

CompletableFuture<Long> countFuture = CompletableFuture.supplyAsync(() ->
        (long) numbers.size()
);

CompletableFuture<Double> combinedFuture = sumFuture.thenCombine(countFuture,
        (sum, count) -> (double) sum / count
);
```

## 사용 시나리오별 가이드

| 시나리오        | 추천 메서드         | 이유            |
|-------------|----------------|---------------|
| 단순 비동기 작업   | `supplyAsync`  | 가장 기본적이고 간단   |
| 결과 변환       | `thenApply`    | 동기적 변환이므로 빠름  |
| 연속 비동기 작업   | `thenCompose`  | 비동기 체이닝으로 효율적 |
| 여러 작업 병렬 실행 | `allOf`        | 모든 결과가 필요할 때  |
| 경쟁 조건       | `anyOf`        | 하나만 성공하면 될 때  |
| 예외 처리       | `handle`       | 안전한 예외 복구     |
| 로깅/정리       | `whenComplete` | 완료 시 콜백 실행    |
| 결과 조합       | `thenCombine`  | 독립적 작업 결과 결합  |

## 성능 팁

1. **CPU 집약적 작업**: 쓰레드 풀 크기를 CPU 코어 수로 제한
2. **데이터 분할**: 작업을 CPU 코어 수만큼 청크로 분할
3. **예외 처리**: `handle`이나 `exceptionally`로 안전하게 처리
4. **리소스 관리**: `ExecutorService` 사용 후 반드시 `shutdown()` 호출

## 주의사항

- `join()`은 블로킹 메서드이므로 주의해서 사용
- 예외 처리를 반드시 구현하여 안정성 확보
- 쓰레드 풀 크기를 작업 유형에 맞게 조정
- 리소스 누수 방지를 위한 적절한 정리 작업 필요
