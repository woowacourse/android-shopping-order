# 상태 코드

- 300: 리다이렉션
- 400: 클라이언트의 잘못된 요청
- 500: 서버의 오류


- HTTP 메서드
    - GET
    - POST
    - DELETE
    - FETCH 가 아니라 PATCH

- 헤더는 request 와 response 둘 다에서 사용된다.

- 100 번대 상태 코드는 임시 응답으로 요청을 받았으며 작업을 계속한다는 의미이다. TRUE
  100 번대는 임시적인 응답이다.

- 201 상태코드는
    - 응답이 반환되기 이전에 새로운 리소스가 생성된다.
    - CREATED 라는 뜻이다.
    - 요청 URL 또는 Location 헤더에 새로 생성된 리소스의 URL을 포함한다.

- REST API 에서 Resource 의 의미
    - 서버에 존재하는 데이터의 표현 + AI 를 통해 접근할 수 있는 모든 데이터.

- REST API. VS RESTful API ?

- RESTful API 올바른 설명
  - 서버 상태를 유지하지 않는다. (Stateless)
  - 세션 정보를 클라이언트에 저장하지 않는다. 키를 저장할 수는 잇따.
  - 데이터는 비동기/동기 적으로 전송할 수 있다.




- 레벨 2 방학 주간에 공부하면 좋을 것들
  - 피드백, 리뷰어 코멘트, 수업(LMS 자료) 복습
  - 네트워크 공부
  - 코루틴 공부(OS 지식 필요할 수도?)

- 메뉴 아이템을 표시하는 방법
```kotlin
class MenuBoardViewModel(...) : ViewModel() {
    val title: LiveData<String> = ...
    val description: LiveData<String> = ...
    val price: LiveData<Int> = ...
    val imageUrl: LiveData<String> = ...
    val isPopular: LiveData<Boolean> = ...
    val isSoldOut: LiveData<Boolean> = ...
    val isRecommended: LiveData<Boolean> = ...
}
```
👇
```kotlin
sealed interface MenuUiState {
    data class Menu(
        val title: String,
        val description: String,
        val price: Int,
        val imageUrl: String,
        val isPopular: Boolean,
        val isSoldOut: Boolean,
        val isRecommended: Boolean,
    ) : MenuUiState

    data class Loading(val isLoading: Boolean) : MenuUiState
    data class Error(val message: String) : MenuUiState
}
```

ui state 를 이렇게 세가지 요소로 추출할 수도 있지만, 다르게 추출할 수도 있다.
도메인에 맞게!

```kotlin
sealed interface MenuUiState {
  data class Available(
    val title: String,
    val description: String,
    val price: Int,
    val imageUrl: String,
    val isPopular: Boolean,
    val isRecommended: Boolean,
  ) : MenuUiState

  data class SoldOut(
    val title: String,
    val description: String,
    val price: Int,
    val imageUrl: String,
  ) : MenuUiState
}
```

장점: soldout 일 때, 인기 메뉴인지, 추천 메뉴인지 상태를 들고 있을 필요가 없다.

단점: 이 때는 타입 체크를 해야 한다. 그렇다면, 데이터 바인딩 환경에서 하기 어렵다.


# Live Coding

```kotlin
sealed interface CartUiState {
    data class Success(
        val cartProducts: List<Product>,
    ): CartUiState
  
  data object Loading: CartUiState
}
```

그러면 뷰모델에서?

```kotlin

val isLoading: LiveData<Boolean> = uiState.map {
    it is CartUiState.Loading
}

```
이런 식으로 map 을 써서 switch map(?) 해주어야 한다.


```kotlin
class CartViewModel(
  private val repo: CartRepository
): ViewModel() {
    private val _uiState = MutableLiveData<CartUiState>()
    val uiState: LiveData<CartUiState> = _uiState
    
    fun getAll(){
        _uiState.value = CartUiState.Loading
        viewModelScope.launch {
            val cartProducts = repo.getAll()
            _uiState.value = CartUiState.Success(cartProducts)
        }
    }
}

```


# Biz logic vs Ui logic




## State Holder?
### Resource Provider

굳이 뷰모델에서 context 를 들고 있지 않아도 ~~ 참조할 수 있도록.

```kotlin
import androidx.annotation.StringRes

interface StringResourceProvider {
    fun getString(@StringRes resourceId: Int, parameter: String): String
}
```


# 예외 처리
## try-catch
```kotlin
fun getAllCartProducts() {
    viewModelScope.launch {
        try {
            val cartProducts = repo.getAll()
            _uiState.value = CartUiState.Success(cartProducts)
        } catch (e: Exception) {
            _uiState.value = CartUiState.Error(e.message ?: "Unknown error")
        }
    }
}
```
try-catch blcok 을 공통 요소로 뽑기

## CoroutineScope  

코루틴에서는 coroutineExceptionHandler 로 활용!!

```kotlin
private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    _uiState.value = CartUiState.Error(throwable.message ?: "Unknown error")
}
```

```kotlin
fun getAllCartProducts() {
    viewModelScope.launch(coroutineExceptionHandler) {
        val cartProducts = repo.getAll()
        _uiState.value = CartUiState.Success(cartProducts)
    }
}
```

또는 
```kotlin
fun getAllCartProducts() {
  (viewModelScope + coroutineExceptionHandler).launch{
    val cartProducts = repo.getAll()
    _uiState.value = CartUiState.Success(cartProducts)
  }
}

```
viewModelScope 와 couroutineExceptionHandler 를 하나의 scope 로 묶어둘 수도 있다.  
레아는 하나의 scope 로 묶어두는 것을 지양한다.  
viewModelScope 가 아닌 다른 스코프를 쓸 수도 있으니까.

### 구조화된 동시성? (structured concurrency)
```kotlin
viewModelScope.launch(coroutineExceptionHandler) {
    try {
        launch { throw RuntimeException("도다른 에러 발생!") }
        val cartProducts: List<Product> = repo.getAll()
        _uiState.value = CartUiState.Success(cartProducts)
    }catch (e: Exception){
        _uiState.value = CartUiState.Error(e.message ?: "Unknown error")
    }
}
```
이렇게 하면 RntimeException("도다른 에러 발생!") 가 안 잡힌다.  
우리는 자식에서 발생한 에러가 부모에게 전달되지 않도록 만들고 싶은 것.  

즉, 자식 RuntimeException 이 발생하더라도, 부모는 정상적으로 실행되도록 하고 싶은 것.

그런데 위처럼 만들면, 부모가 자식의 에러를 잡아내지 못한다.  
cancel 이 되는 것과 같다.  

그래서 이런 경우에는 **supervisorJob** 을 사용한다.

```kotlin
fun getAllCartProducts() {
    viewModelScope.launch(coroutineExceptionHandler) {
        supervisorScope {
            launch {
                throw RuntimeException()
            }
            val cartProducts = cartRepository
                .getAllCartProducts()
                .map { it.toModel() }
            _cartProducts.value = CartUiState.Success(cartProducts)
        }
    }
}
```

양방향 취소!! 단방향 취소!!



# Http 에러

만약 Result 로 감싸지 않는다면?  
```kotlin
suspend fun getAll(): List<Product> {
    val response = api.getAll()
    if (response.isSuccessful) {
        return response.body() ?: emptyList()
    } else {
        throw HttpException(response)
    }
}
```
이것의 단점?  
데이터 소스가 HttpException 이라는 예외 타입을 알고 있는 것은 괜찮다.  
그런데 레포로 이게 넘어가면?  레포지토리가 HttpException 이라는 예외를 알 필요가 있나?  
