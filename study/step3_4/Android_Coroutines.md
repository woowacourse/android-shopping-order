# ViewModel 에서 코루틴

```kotlin
@Dao
interface CartProductDao {
    @Query("SELECT * FROM cart_products")
    suspend fun getAll(): List<CartProductEntity>
}
```

```kotlin
class CartRepository(
    private val dao: CartProductDao,
) {
    suspend fun getAllCartProducts(): List<CartProductEntity> {
        return dao.getAll()
    }
}
```

```kotlin
class CartViewModel(
    private val cartRepository: CartRepository,
) : ViewModel() {

    private val _cartProducts: MutableLiveData<List<Product>> =
        MutableLiveData(emptyList())
    val cartProducts: LiveData<List<Product>> get() = _cartProducts

    fun getAllCartProducts() {
        viewModelScope.launch {
            val cartProducts = cartRepository
                .getAllCartProducts()
                .map { it.toModel() }
            _cartProducts.value = cartProducts
        }
    }
}
```

## 왜 Dispatchers.IO 를 명시하지 않아도 잘 작동하나?

- Dispatchers.IO 는 I/O 작업을 처리하기 위한 디스패처이다. (예를 들어, 데이터베이스 접근, 네트워크 호출)
- Dispatchers.Main 은 메인 스레드 작업을 처리하기 위한 디스패처이다. (예를 들어, UI 업데이트)
- Dispatchers.Default 는 CPU 기능이 많이 필요한 작업을 처리하기 위한 디스패처이다.

### Room 과 코루틴의 암묵적인 동작

CartProductDao 함수의 getAll() 은 suspend function.  
코루틴에서 dao.getAll() 을 호출할 때, 코루틴 디스패처는 Room에 의해 암묵적으로 처리된다.

### 왜 Dispatchers.IO 를 명시하지 않아도 잘 작동하나?

룸의 코루틴 서포트: R
oom의 suspend 함수는 자동으로 실행되는 스레드를 처리한다.
dao.getAll()을 호출할 때 Room은 내부적으로 I/O 디스패처를 사용하여 데이터베이스 쿼리를 수행한다.
따라서 Room의 suspend 함수를 사용할 때 Dispatchers.IO를 명시적으로 지정할 필요가 없다.

default dispatcher 의 뷰모델 스코프:
뷰모델 스코프는 기본적으로 Dispatchers.Main을 사용한다.  
Room은 데이터베이스 작업을 내부적으로 I/O 디스패처로 오프로드하기 때문에,  
뷰모델 수준에서 Dispatchers.IO를 지정할 필요가 없다.

그래서 코루틴을 뷰모델 스코프에서 실행할 때,
메인 스레드에서 호출하더라도, 실제 DB 작업은 Room 에 의해 백그라운드로 이동한다.  
이렇게 하면 데이터베이스 작업이 블로킹되지 않고, Dispatchers.IO를 수동으로 지정할 필요가 없다.

**뷰모델의 getAllCartProducts 가 viewModelScope.launch 에서 불리고 있다.
이는 기본적으로 Dispatchers.Main 에서 동작한다.
Room 라이브러리는 getAll() 메소드에 대해 내부적으로 I/O 스레드를 관리한다.
그래서 Dispatchers.IO 를 명시적으로 지정하지 않아도 된다.**

## postValue()가 아닌 setValue()를 사용한 이유

알고 있던 내용:

- `setValue`: 동기적, 메인 스레드에서 동작하는 코드임이 확실할 때 사용
- `postValue`: 비동기적, 어떤 스레드에서든 사용 가능

viewModelScope.launch 는 기본적으로 Dispatchers.Main 에서 동작한다.  
딜레이 없이 UI 가 즉시 업데이트되어야 하므로, setValue() 를 사용하는 것이 적절하다.

이미 메인 스레드에서 싱행 중이다.  
LiveData 를 즉시 업데이트하므로, UI 가 지연 없이 최신 상태로 유지된다.  
만약 백그라운드 스레드에서 실행 중이라면 postValue() 를 사용해야 한다.

viewModelScope.launch 를 Dispatchers.IO 에서 사용하지 않고,  
위 예시처럼 암시적으로 Dispathcers.Main 에서 사용했을 떄.  
setValue 를 사용하자!

## 결론

- Dispatchers.IO 없이 동작하는 이유
    - Room은 suspend 함수를 관리하며, Dispatchers.IO 를 내부적으로 사용한다.
- postValue() 대신 setValue()를 사용한 이유
    - Dispatchers.Main 에서 실행 중이므로, setValue()를 사용하면 UI가 즉시 업데이트된다.

# apiService 의 추상화?

Retrofit 을 사용한 api Service 는 suspend 키워드를 붙이면, 자동으로 io dispatcher 에서 실행돼.

그래서 data source 에서 dispathcer 를 지정해줄 필요가 없어.

그런데 aspi Service 를 추상화한 인터페이스에 의존한다고 하자.

그렇게 되면 현재 데이터 소스 코드에서 실제 apiService interface 의 구현체에 따라 비동기적으로 동작할 수도, 아닐 수도 있어.

만약 apiService 를 Retrofit 이 아닌 다른 lib. 를 쓴다고 하고, 여전히 통신 작업은 io 디스패처에서 동작하도록 만들고 싶다면, 문제가 생겨.

교체한 lib 에서 dispatchers.io 에서 동작하도록 만들 수 없다면 데이터 소스의 구현체에서 io dispatcher 를 사용하도록 바꾸어 줘야 하잖아.

lib 변경으로 데이터 소스가 변경되네?  
이거 별로 아님?

비동기 동작을 핸들링하기 위한 크리티컬 포인트를 제기했어.  
비동기 작업을 항상 적절한 디스패처에서 실행되도록 보장해야 해.  
어떤 라이브리리나 디테일한 구현에 의존하지 않고 말이야.  
너의 걱정을 분석해보고, 강력한 솔루션을 제공해보자.

## 문제 상황

- 현재 이슈
    - Retrofit 을 사용하면, CartItemApiService 의 suspend 함수는 자동으로 Dispatchers.IO 에서 실행돼.
    - CartItemRemoteDataSource 는 이를 활용해, 디스패처를 명시적으로 지정하지 않아도 돼.

- 잠재 이슈
    - 다른 라이브러리로 전환하면, CartItemRemoteDataSource 구현체가 Dispatchers.IO 에서 작업을 실행하지 않을 수 있어.
    - 이러한 불일치는 성능과 응답성에 문제를 일으킬 수 있어.
    - 해결을 위해 데이터 소스에서 비동기 동작을 하도록 변경하면, 통신 라이브러리 변경이 데이터 소스로 전파되는 문제가 생겨.

### 일관된 디스패처 사용 보장하기

- 데이터 소스 레이어에서 디스패처를 명시적으로 지정하는 방법을 사용해, 모든 네트워크 또는 무거운 작업이 항상 적절한 디스패처에서 실행되도록 보장해.
- 어떠한 네트워크 호출 라이브러리를 사용하더라도, 데이터 소스 레이어에서 항상 Dispatchers.IO 에서 작업을 실행하도록 만들어.

### 해결책: 데이터 소스에서 디스패처 처리 캡슐화

- 데이터 소스 레이어에서 항상 Dispatchers.IO 에서 작업을 실행하도록 보장하기 위해, 데이터 소스 메소드 내부에서 `withContext(Dispatchers.IO)` 를 사용해.
- 그러면, 디스패처를 데이터 소스에서 주입해주어야 겠다.

### Explanation

1. `withContext(Dispatchers.IO)`
    - 이는 코루틴을 일시 중단하고, `Dispatchers.IO` 컨텍스트로 전환해, 블록 내의 코드가 IO 디스패처에서 실행되도록 보장해.
    - `runCatching` 블록을 감싸, 내부의 모든 작업이 적절한 백그라운드 스레드에서 실행되도록 보장해.

2. 변화에 대한 탄력성
    - 미래에 다른 라이브러리나 메커니즘으로 API 호출을 전환하더라도, `CartItemRemoteDataSource` 는 데이터 가져오기 로직이 `Dispatchers.IO`에서 실행되도록 보장해.
    - 이 접근 방식은 `CartItemApiService`의 구현 세부 사항을 추상화해, 기반이 되는 API 서비스 구현의 변경에도 데이터 소스가 백그라운드 스레드에서 실행되도록 보장해.

### 이렇게 하면 뭐가 좋은데?

- **일관된 실행 컨텍스트**: 데이터 소스 내의 모든 I/O 작업이 항상 백그라운드 스레드에서 수행되도록 보장해, API 서비스 구현체에 관계없이 일관된 동작을 보장해.
- **결합도 낮추기**: 뷰모델 및 코드의 다른 부분이 디스패처 관리 방법을 인식할 필요가 없어, 코드를 더 깔끔하고 유지보수하기 쉽게 만들어.
- **유연성**: 다른 API 서비스 구현으로 전환해도, 상위 레이어에 스레딩 관련 문제가 노출되지 않아, 애플리케이션의 다른 부분에 영향을 미치지 않고 스레딩 관심사를 처리할 수 있어.

### 테스트에서 고려 사항

테스트에서 일관성을 유지하기 위해, 테스트 디스패처를 주입해, 테스트가 일관되고 실제 I/O 작업 없이 빠르게 실행되도록 보장해.

### Testing Considerations

For testing, you can inject a test dispatcher to ensure your tests run consistently and without real I/O operations.
This maintains a clear separation between production and test environments, ensuring that the tests accurately reflect
production behavior while remaining isolated and fast.

```kotlin
class TestCartItemRemoteDataSource(
    private val cartItemApiService: CartItemApiService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO  // Default to IO for production, but can be overridden in tests
) : ShoppingCartDataSource {
    override suspend fun findByProductId(productId: Long): Result<ProductIdsCountData> =
        withContext(dispatcher) {  // Use injected dispatcher
            runCatching {
                val allCartItems = cartItemApiService.requestCartItems2().content
                val find =
                    allCartItems.find { it.product.id == productId }
                        ?: throw NoSuchElementException("there is no product with id $productId")
                ProductIdsCountData(find.product.id, find.quantity)
            }
        }
}
```

### 결론

데이터소스를 다루는 디스패처를 캡슐화해서, 다른 구현체, 다른 환경에서도 일관된 동작을 보장할 수 있다.  
이는 성능, 유지보수, 테스트 가능성을 향상시킨다.

[실제로 안드 공식 문서에서도 ApiService 를 구현체에 관계 없이 인터페이스로 추상화한다!](https://developer.android.com/topic/architecture/data-layer?hl=ko#create_the_data_source)

> Key Point: Relying on interfaces makes API implementations swappable in your app.
> In addition to providing scalability and allowing you to replace dependencies more easily,
> it also favors testability because you can inject fake data source implementations in tests.

# 코루틴 테스트하기

## 아래 두개의 차이는??

1.

```kotlin
private val dispatcher: CoroutineDispatcher = Executors
    .newSingleThreadExecutor()
    .asCoroutineDispatcher()
```

2.

```kotlin
private val dispatcher: CoroutineDispatcher = UnconfinedDispatchers()
```

### `Executors.newSingleThreadExecutor().asCoroutineDispatcher()`

```kotlin
private val dispatcher: CoroutineDispatcher = Executors
    .newSingleThreadExecutor()
    .asCoroutineDispatcher()
```

1. **Single-Threaded Execution**:
    - 이렇게 하면, 코루틴을 실행할 전용 스레드를 제공하는 단일 스레드 executor 가 생성된다.
    - 이는 `Dispatchers.IO` 나 `Dispatchers.Default` 와 유사한 단일 스레드 환경을 시뮬레이션하고자 할 때 적합하다.

2. **Concurrency Control**:
    - 단일 스레드 executor 를 사용하면, 코루틴이 순차적으로 실행되어, 다른 스레드가 지정되지 않는 한, 한 번에 하나씩 실행된다.
    - 예측 가능한 실행 순서를 원하는 테스트에 유용하다.

3. **Performance**:
    - 이는 `Dispatchers.Unconfined` 와 같은 공유 디스패처를 사용하는 것보다 느릴 수 있다. 왜냐하면 전용 스레드를 관리하는 오버헤드가 발생하기 때문이다.
    - 코루틴이 실행되는 스레드에 대해 정확한 제어가 필요한 경우 유용하다.

4. **Deterministic Scheduling**:
    - 더 예측 가능한 스케줄링을 제공하며, 복잡한 코루틴 상호 작용을 디버깅하고 테스트하는 데 유용하다.

5. **Resource Management**:
    - 테스트 후 리소스 누수를 방지하기 위해 수동으로 executor 를 종료해야 한다. 이는 일반적으로 테스트 클래스의 `@AfterEach` 또는 `@After` 메소드에서 수행된다.

### `Dispatchers.Unconfined`

```kotlin
private val dispatcher: CoroutineDispatcher = Dispatchers.Unconfined
```

1. **Immediate Execution**:
    - `Dispatchers.Unconfined` 는 호출자 스레드에서 코루틴을 시작하며, 코루틴이 일시 중단될 때만 다른 스레드로 전환한다.
    - 이는 다른 스레드가 지정되지 않으면 코루틴이 재개된 후 호출자 스레드에서 계속 실행될 수 있다.

2. **Testing Simplicity**:
    - `Dispatchers.Unconfined` 는 코루틴을 즉시 실행하고 동기적으로 실행하므로, 테스트 코드를 단순화하는 데 유용하다.
    - 테스트에서 코루틴이 즉시 실행되어 동기적으로 실행되므로, 테스트 코드를 단순화하는 데 유용하다.

3. **Not Suitable for Production**:
    - 테스트에서 유용하지만, `Dispatchers.Unconfined` 는 일반적으로 프로덕션 코드에는 적합하지 않다.
    - 왜냐하면 스레드 전환을 강제하지 않기 때문에, 병렬 환경에서 예측할 수 없는 동작을 일으킬 수 있기 때문이다.

4. **Direct Execution**:
    - 호출자 스레드에서 실행되므로, 간단한 테스트에서 실행이 더 예측 가능하며, 비동기 스케줄링과 관련된 잠재적인 문제를 피할 수 있다.

5. **Less Overhead**:
    - 추가 스레드를 생성하거나 관리하지 않기 때문에, 간단한 코루틴 테스트에 대해 가벼우며 빠르다.

### Choosing Between the Two

- `Executors.newSingleThreadExecutor().asCoroutineDispatcher()` 을 써야 될 때!
    - `Dispatchers.IO` 나 다른 멀티 스레드 환경에 더 가까운 환경을 시뮬레이션하고자 할 때
    - 특정, 제어된 스레딩 환경에서 테스트가 실행되도록 보장하고자 할 때
    - 테스트 시나리오에 대한 스레딩과 스케줄링에 대한 정확한 제어가 필요할 때
    - 프로덕션에서 전용 백그라운드 스레드에서 실행되는 코드를 테스트할 때

- `Dispatchers.Unconfined` 써야 할 때:
    - 테스트를 단순화하고 동기적으로 실행하고자 할 때
    - 테스트 시나리오가 복잡하지 않고, 스레드 관리에 대한 추가적인 제어가 필요하지 않을 때
    - 테스트가 빠르게 실행되도록 보장하고, 스레드 관리에 대한 추가적인 제어가 필요하지 않을 때
    - 특정 스레드 관리에 대한 추가적인 제어가 필요하지 않을 때

### 테스트에서 리소스 관리 예

`Executors.newSingleThreadExecutor().asCoroutineDispatcher()` 를 사용하는 경우, executor 를 shut down 함으로써 executor 를 적절히 관리해야 한다.


```kotlin
class DefaultShoppingCartRepositoryTest {
    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private lateinit var castSource: ShoppingCartDataSource
    private lateinit var repository: ShoppingCartRepository

    @BeforeEach
    fun setUp() {
        castSource = FakeShoppingCartDataSource(dispatcher = dispatcher)
        repository = DefaultShoppingCartRepository(castSource)
    }

    @AfterEach
    fun tearDown() {
        (dispatcher.executor as? ExecutorService)?.shutdown()
    }

    // Your test cases go here...
}
```
이 설정에서 `dispatcher.executor` 는 `ExecutorService` 로 캐스팅되어 `shutdown()` 을 호출해, 각 테스트 후에 executor 가 닫힌다.  
