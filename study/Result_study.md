# Result

Result 는 동작이 성공 or 실패할 수 있는 동작의 결과를 나타내는 데 쓰이는 클래스.

map 과 mapCatching 함수는 Result 객체의 캡슐화된 값을 변환하는 메커니즘을 제공한다.
두 함수는 성공적인 결과를 변환하거나 실패 케이스를 다르게 처리하는 데 사용된다.

Result 사용 의 주 의도,목적은 성공과 실패를 try-catch 와 같은 명시적인 에러 핸들링 없이 처리하는 것이다.

### Explanation of `map` and `mapCatching`

#### 1. `map`

**Signature:**

```kotlin
public inline fun <R, T> Result<T>.map(transform: (value: T) -> R): Result<R>
```

- 목적: map 은 성공적인 Result 의 캡슐화된 값을 변환 함수에 적용하는 데 사용된다.
  Result 가 실패하면 변환을 적용하지 않고 원래 실패를 반환한다.
- 동작: 변환 함수가 던질 수 있는 예외를 다시 던진다.
- 사용 시나리오: 성공적인 결과를 변환하고 변환 함수가 예외를 던지지 않을 것이라고 확신할 때
  또는 예외가 전파되기를 원할 때 `map` 을 사용한다.

**작동 방식:**

- **contract:** `transform` 함수가 최대 한 번 호출될 것이라고 명시한다.  (제약!)
- **구현:**
    - `Result` 가 성공을 나타내면 캡슐화된 값을 변환 함수에 적용한다.
    - `Result` 가 실패를 나타내면 캡슐화된 예외를 그대로 반환한다

**Code Explanation:**

```kotlin
public inline fun <R, T> Result<T>.map(transform: (value: T) -> R): Result<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return when {
        isSuccess -> Result.success(transform(value as T))  // Applies transform if successful
        else -> Result(value)                               // Returns original exception if failure
    }
}
```

**Example:**

```kotlin
val result: Result<Int> = Result.success(2)
val transformedResult: Result<String> = result.map { it.toString() }
println(transformedResult) // Output: Success(kotlin.String)
```

#### 2. `mapCatching`

**Signature:**

```kotlin
public inline fun <R, T> Result<T>.mapCatching(transform: (value: T) -> R): Result<R>
```

- **목적:** `mapCatching` 은 `map` 과 유사하지만 내장된 예외 처리가 있다. 성공적인 `Result` 의 캡슐화된 값을 변환 함수에 적용하고 변환 중 발생하는 예외를 실패 `Result` 로
  캡슐화한다.
- **동작:** 변환 함수가 던질 수 있는 예외를 잡아서 실패 `Result` 로 변환한다.
- **사용 시나리오:** 변환 함수가 예외를 던질 수 있고, 그 예외를 전파시키지 않고 실패로 캡처하고 싶을 때 `mapCatching` 을 사용한다.

작동 방식

- 구현
  - Result 가 성공이면, runCatching 을 사용하고, 안전하게 transform 을 적용
  - Result 가 실패이면, 간단히 캡슐화된 예외를 리턴.


```kotlin
public inline fun <R, T> Result<T>.mapCatching(transform: (value: T) -> R): Result<R> {
    return when {
        isSuccess -> runCatching { transform(value as T) }  // Applies transform with exception handling
        else -> Result(value)                               // Returns original exception if failure
    }
}
```

**Example:**

```kotlin
val result: Result<Int> = Result.success(2)
val transformedResult: Result<String> = result.mapCatching {
    if (it == 2) throw IllegalArgumentException("Exception!")
    it.toString()
}
println(transformedResult) // Output: Failure(kotlin.IllegalArgumentException: Exception!)
```

### 요약
- map 은 성공한 result 을 변환시키고, 예외는 다시 던진다.
- mapCatching 은 성공한 결과를 변환시키고, 실패한 예외는 잡아서 캡슐화한다.

이 함수들은 Result 의 동작을 체이닝하는 강력한 도구.  
에러를 이쁘게 처리할 수 있다.  


## runCatching

runCatching 은 예외를 던질 수 있는 동작을 처리하는 데 사용되는 유틸리티 함수.  
동작의 결과를 캡처하고 성공적인 결과나 예외를 나타내는 Result 객체를 반환한다.

- **목적**: 코드 블록을 실행하고 그 결과를 `Result` 객체에 캡슐화하여 예외를 이쁘게 처리한다.
- **반환 타입**: `Result<T>` 여기서 `T` 는 성공적인 결과의 타입이다.
- **예외 처리**: 코드 블록이 예외를 던지면 `runCatching` 이 예외를 잡아서 `Result.Failure` 로 캡슐화한다.
- **사용 시나리오**: 예외를 던질 수 있는 동작을 안전하게 실행하고, 성공과 실패를 구분하여 처리할 때 사용한다.
- **주요 기능**: 성공과 실패를 캡슐화하고, `Result` 의 `map`, `mapCatching`, `onSuccess`, `onFailure` 등의 메서드를 사용하여 결과를 처리한다.

### `runCatching`의 시그니처

```kotlin
inline fun <R> runCatching(block: () -> R): Result<R>
```

### 동작 방식
- **실행**: `runCatching` 내에서 코드 블록이 실행된다.
- **성공**: 코드 블록이 예외를 던지지 않고 완료되면, 결과는 `Result.Success` 로 래핑된다.
- **실패**: 코드 블록이 예외를 던지면, 예외가 잡히고 `Result.Failure` 로 래핑된다.

### 사용 예시

```kotlin
val result = runCatching {
    // Some operation that might throw an exception
    "This might fail".toInt()
}

result.onSuccess { value ->
    println("Success: $value")
}.onFailure { exception ->
    println("Failed with exception: ${exception.message}")
}
```

- runCatching 의 주요 기능
- **안전성**: 결과와 예외를 모두 캡슐화하여 예외가 애플리케이션을 중단시키지 않도록 한다.
- **체이닝**: `Result` 의 `map`, `mapCatching`, `onSuccess`, `onFailure` 등의 메서드를 사용하여 추가 작업을 체이닝할 수 있다.
- **간소화**: 명시적인 try-catch 블록이 필요 없어져 코드가 단순해진다.

- 레포지토리에서 `runCatching` 사용

DefaultShoppingProductRepository 에서 `runCatching` 을 사용하면 함수를 안전하게 실행하고 발생할 수 있는 예외를 처리할 수 있다.  

```kotlin
override suspend fun loadProduct(id: Long): Result<Product> {
    return runCatching {
        // Assuming this function could potentially throw an exception
        val productData = productsSource.findById(id)
        productData.toDomain(productQuantity(id).getOrThrow())
    }
}
```
여기에서 
- `runCatching` 은 Product 을 로드하고 변환하는 작업을 래핑한다.
- `findById` 또는 `toDomain` 이 예외를 던지면 `runCatching` 이 예외를 캡처.
- 결과 `Result<Product>` 객체는 작업이 성공했는지 실패했는지를 나타낸다.

### Advanced Usage with Functional Methods
runCatching 이 리턴하는 Result 타입 은 `map`, `flatMap`, `recover` 등의 함수를 사용하여 추가 처리가 가능하다. 


```kotlin
val result = runCatching {
    "123".toInt()  // This will succeed
}.map { value ->
    value * 2
}.recover { exception ->
    -1  // Provide a default value in case of failure
}

println(result.getOrDefault(-999))  // Outputs: 246
```

- **`map`**: 성공적인 결과를 변환한다.
- **`recover`**: 실패 시 대체 값을 제공한다.

### 요약
- **`runCatching`**: 예외를 던질 수 있는 동작을 안전하게 실행하고, 성공과 실패를 구분하여 처리하는 데 사용된다.
- **캡슐화**: 성공과 실패를 `Result` 객체로 캡슐화하여 예외를 처리한다.
- **통합**: `map`, `flatMap`, `recover` 등의 함수를 사용하여 결과를 처리하는 함수형 프로그래밍 개념을 쉽게 적용할 수 있다.
