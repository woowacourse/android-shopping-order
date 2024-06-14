```kotlin
viewModelScope.launch{
    // ...
    _liveData.value = newLiveData
}
```
그리고, Repo 와 DataSrc, RoomDao/혹은 RetrofitApiService 에서 suspend 함수!

이것이 가능한 이유, 이렇게 해야하는 이유는 [Android_Coroutines.md](Android_Coroutines.md) 참고.

의문 CHAIN
- 🙋 Room 이나 Retrofit 작업은 Dispatchers.IO 에서 실행되어야 하는 것 아닌가?
  - 맞다! Room/Retrofit 함수에서 suspend 함수를 사용하면, Dispatchers.IO 에서 실행된다.
- 🙋그러면 Room 이나 Retrofit 이 아닌 다른 라이브러리를 사용하도록 변경된다고 하자,  
그리고 변경된 라이브러리가 suspend 키워드를 붙이는 것만으로 Dispatchers.IO 에서 실행되지 않는다고 하자.   
그렇다면 결국 뷰모델의 코드를 `viewModelScope.launch(Dispatchers.IO)` 로 바꿔야 하는 것 아닌가??? 
서버 통신, 영속성 저장 라이브러리를 변경했는데 변경 사항이 뷰모델까지 전파가 되네????? 
  - 아니다! 다른 라이브러리를 사용하더라도, 데이터 소스에서 Dispatcher 를 IO 로 지정할 수 있기 때문이다.  
  - 그렇게 되면 라이브러리 변경이라는 변경사항이 뷰모델까지 전파되지 않는다.
  - 실제로 [안드 공식 문서](https://developer.android.com/topic/architecture/data-layer?hl=ko#create_the_data_source)에  


테스트 관련 고찰
- 우리의 production 코드에서는 Room 이나 Retrofit 을 쓰고 있다.  
- 그래서 Dispatchers.IO 를 명시하는 부분이 없다.  
- Repo 와 뷰모델 테스트에서는 FakeDataSource 를 써야 할 것이다.  
- 그런데 ViewModel 에서는 ViewModelScope 가 Dispatchers.Main 이다.
- 그래서 Repo 나 DataSrc 에서 Dispatcher 를 주입함으로써 Production 과 Test 환경의 UnMatching 이 해결된다!!!!

---

### 마지막 의문점
- 🙋 그런데 FakeDataSource 에서 Dispatcher 를 지정해주지 않는 것이 문제가 되나?????

내 생각: 실제로 테스트 자체는 통과할 것이다.  
레포지토리를 테스트하던, 뷰모델을 테스트하던지,  
디스패처가 다를 뿐, 동작은 같다. 단지 어떤 스레드에서 일을 할지만 달라지는 것이다.  
그래서 테스트는 통과할 것이다.  

그러나, 
프로뎍선 환경에서는 repo 나 data src 의 일을 Dispatchers.IO 에서 하는 반면에  
테스트 환경에서는 repo 나 data src 의 일을 Dispatchers.Main 에서 하는 것 자체가 문제가 된다.  

테스트를 하려면 최대한 환경이 같아야 한다.  
그래서 FakeDataSource 에서도 Dispatchers.IO 로 지정해주는 것이 맞다.  
그러려면 Dispatcher 를 주입해주면 된다!!!!  


제임스에게 여쭈어보기: 디스패처가 다른 것 만으로 테스트가 통과되지 않는 케이스가 있을 것이다!!!!!  

### 마지막 의문점을 gpt 에게 물어보기  

Why Match Dispatchers in Testing?
While the tests may pass even if the dispatchers differ between production and test environments, there are several reasons why aligning them is crucial:

- **Consistency**: Using the same dispatchers in tests and production ensures that the tests accurately reflect the production environment’s behavior.

- **Timing Issues**: Different dispatchers can introduce timing discrepancies.
For example, Dispatchers Main is optimized for UI updates and has different scheduling priorities than Dispatchers. IO, 
potentially affecting the timing and order of operations.

- **Thread Affinity**: Certain tests may rely on the thread affinity of the operations, 
especially when dealing with UI components or certain synchronization primitives.
Misalignment can cause flaky tests or unexpected behavior.

Injecting Dispatchers
To achieve consistent behavior, you can inject dispatchers into your repositories or data sources. This allows you to control the execution context both in production and during tests.

```kotlin
class CartRepository(
    private val dao: CartProductDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO // Default to IO for production
) {

    suspend fun getAllCartProducts(): List<CartProductEntity> {
        return withContext(dispatcher) {
            dao.getAll()
        }
    }
}
```

In your tests, you can inject a different dispatcher, such as Dispatchers.Unconfined, to match the test environment.

```kotlin
@Test
fun testGetAllCartProducts() = runBlockingTest {
    val testDispatcher = TestCoroutineDispatcher()
    val repository = CartRepository(fakeDao, testDispatcher)
    // Your test code here
}
```

This method maintains the isolation of threading logic and avoids modifying the ViewModel for testing purposes.

Conclusion
Room/Retrofit: Automatically handle threading for suspend functions, offloading work to Dispatchers.IO internally.
Changing Libraries: If using a library that doesn’t handle threading,  
encapsulate the dispatcher management within the repository or data source to prevent changes from propagating to the ViewModel.
Testing: Ensure dispatchers are consistent between production and test environments by injecting them,  
avoiding discrepancies and maintaining test reliability.

By following these practices,  
you ensure that your application is robust, scalable, and easy to maintain and test.


## 다시 생각해본 점.

데이터 소스에서 디스패처를 지정해주는 게 옳은가???  
그것보다는 사실 API Service 에서 지정해주면 될 것 같다.

만약 OkHttp3 에서 제공하는 mock 서버를 사용해서 가짜 ApiService 를 만든다면,  
그 구현체에서 IO Dispatcher 에서 동작하도록 지정해주면 될 것이다.  

그래서 미션 구현에서는 데이터 소스가 아닌, Api Service 에서 Dispatcher 를 지정해주는 것으로 설정했다

대신 테스트용 mock 서버를 만드는 것은 너무 오래 걸릴 것 같아서, 테스트할 때는  
디스패처를 데이터 소스에서 주입받도록 했다.  

만약 여유가 있다면 APi Service 에서 구현체를 주입해주는 게 좋을 것 같다.
