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