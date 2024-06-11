# android-shopping-order

### 기능 요구 사항

- [x] 데이터가 로딩되기 전 상태에서는 스켈레톤 UI를 노출한다.
- [x] 서버를 연동한다.
- [x] 사용자 인증 정보를 저장한다. (적절한 저장 방법을 선택한다)
- [x] 장바구니 화면에서 특정 상품만 골라 주문하기 버튼을 누를 수 있다.
- [x] 별도의 화면에서 상품 추천 알고리즘으로 사용자에게 적절한 상품을 추천해준다.
- [x] 상품 추천 알고리즘은 최근 본 상품 카테고리를 기반으로 최대 10개 노출한다.
    - [x] 예를 들어 가장 최근에 본 상품이 fashion 카테고리라면, fashion 상품 10개 노출
    - [x] 해당 카테고리 상품이 10개 미만이라면 해당하는 개수만큼만 노출
    - [x] 장바구니에 이미 추가된 상품이라면 미노출
- [x] 추천된 상품을 해당 화면에서 바로 추가하여 같이 주문할 수 있다.

# 낙서장

## DataSource 와 Repository

DataSource와 Repository는 데이터 관리 계층에서 중요한 역할을 하는 두 가지 개념이다.

### DataSource

DataSource는 데이터 소스와의 직접적인 상호작용을 담당한다. 이는 데이터의 출처에 따라 분류될 수 있다.
이번 미션에서느 Local과 Remote로 나누어 안드로이드 내부/외부에 대한 데이터 CRUD하도록 했다.

### Repository

Repository는 여러 DataSource를 결합하고, 비즈니스 로직을 처리하며, ViewModel이나 UseCase와 같은 상위 계층에 일관된 데이터 접근 방법을 제공한다.
Repository에서는 로컬 및 원격 데이터 소스에서 데이터를 가져와 통합하거나 필요한 비즈니스 로직을 수행한다.

### DataSource 와 Repository를 왜 나누어야 할까?

DataSource와 Repository를 나누는 이유는 코드의 구조화와 유지보수성을 높이기 위해서이다.
이를 통해 관심사를 분리하고, 코드 재사용성과 테스트 용이성을 향상시킨다.
또한, 변경 사항이 특정 클래스에 국한되므로 유지보수가 쉽고, 유연성과 확장성을 제공한다.
이로써 각 컴포넌트는 독립적으로 관리되고, 데이터 소스의 변경이 다른 부분에 영향을 미치지 않는다.

이번 미션에서 위에서 언급한 이점을 경험했다.
쇼핑 장바구니에서 쇼핑 주문으로 코드를 마이그레이션할 때, 장바구니 데이터를 로컬 DB에서 서버 API 호출로 전환해야 했다.
이 과정에서 DataSource와 Repository를 잘 분리해 둔 덕분에, DataSource의 형태를 DB에서 API 호출로 데이터를 가져오는 것으로 수정하는 것만으로 이전과
동일한 기능을 유지할 수 있었다.
이 경험은 코드의 유연성과 확장성, 유지보수성을 크게 향상시켰음을 실감한 순간이었다.

## 설계의 중요성

이번 미션을 하면서 설계가 매우 매우 중요하다는 것을 깨달았다.
깨닫게 한 사건은 다음과 같다.

```
- 장바구니에 상품을 담기 위해서는 `product id`를 `request`로 넘겨야 한다.
- 장바구니에 담긴 상품의 수량을 변경하기 위해서는 `cart id`를 `request`로 넘겨야 한다.
```

위와 같이 API 스펙이 주어졌을 때, 상품 목록에 대한 데이터를 `cartId`, `product`, `quanutity` 를 포함하고 있는 `Cart`로 지정했다.
결국, 장바구니에 없는 상품은 `cartId`, `quantity`를 디폴트 파라미터로 초기화했고, 장바구니에 있는 상품은 해당 상품의 `cartId`와 `quantity`로
초기화 했다.
나는 단순히 간단하게 구현하기 위해 위처럼 도메인을 설계했고, 덕분에 쉽게 로직을 구현할 수 있었다.

그러나 위처럼 개발을 쉽게 하기 위해 도메인을 설계하고, 설계에 대한 설명도 없다면(사실 셜명이 있어도 이해하지 못할 수 있다), 다른 개발자가 봤을 때 납득하지 못하는 부분이
생길 수 있다.

- 상품 목록이기 때문에 당연히 상품 목록에 대한 아이템이 `Product`라고 예상하겠지만, 그것이 아닌 `Cart`라는 것에서 의문이 들 수 있다.
- 상품을 장바구니에 추가했을 때 장바구니에 상품을 담는다는 로직으로 예상하겠지만, 이미 있는 아이템에 `cartId`를 셋업해주는 게 어색하다고 생각할 수 있다.

위 같이 다른 개발자가 처음 봤을 때 이상하다고 느낄 수 있는 부분이 있으며 언급한 것보다 더 많을 수 있다.
그 외에도 내가 봤을 때 이해할 수 있으니 다른 사람이 봐도 이해할 수 있겠지?라고 생각하며 구현한 로직들이 상당수 있었다.

예전에 프로젝트를 설명할 때나 자소서에 나의 경험을 쓸 때도 비슷한 코멘트를 받은 적이 있었다.
프로젝트나 경험은 내가 직접 했기에 내가 말하면서도 쉽게 이해할 수 있지만, 다른 사람이 그것을 보고 들었을 때는 무슨 이야기인지 이해하지 못할 수 있다.

누군가가 듣고 보았을 때도 이해할 수 있는 언어로 말하고 코드를 작성해야할 것 같다.
다시 한번 설계의 중요성을 알게 되었다.

## 로딩 상태를 Base에서 관리하는 것은, 편리할 수 있지만 매우 위험한 문제를 가지고 있다. 그럼 우리는 이 문제를 어떻게 해결해 수 있을까?

문제는 다음과 같다.

```
가령, 로딩이 필요한 비즈니스 로직이 한번에 5개의 요청이 들어왔다고 가정 해봅시다.
그렇다면 각각 함수에서는 showLoading()을 5번까지 요청을 합니다.
이어서 하나의 함수라도 요청이 끝난다면 hideLoading()을 부르게 됩니다.

그러면 아직 4개의 함수가 끝나지 않았는데 UI가 보이는 문제로 이어집니다.
여러가지 솔루션이 있지만, 지금 생각하기에는 난이도가 다소 높을 지 모릅니다.
```

지금 당장 생각나는 솔루션은 로딩 상태를 Base에서 관리하는 것이 아니라 각 페이지별로 각 컴포넌트의 로딩을 관리하는 것이다.
즉, 페이지에서 로딩이 필요한 컴포넌트마다 개별적으로 로딩 상태를 관리하는 것이다.
모바일 특성상 로딩이 필요한 컴포넌트가 많지 않을 것으로 예상되므로, 이러한 방식이 문제를 해결할 수 있는 방법이 될 것 같다.

## 비즈니스 로직을 어디서 처리하는 게 좋을까?(ViewModel vs Repository)

## 비동기를 어디서 처리하는 게 좋을까?(ViewModel vs Repository)

## 코루틴 어디까지 알아?

### 코틀린에서 코루틴이란?

[코틀린 공식문서](https://kotlinlang.org/docs/coroutines-overview.html)에서는 코루틴을 다음과 같이 설명한다.

```
비동기 또는 논블로킹 프로그래밍은 서버, 데스크탑 또는 모바일 애플리케이션을 개발할 때 중요한 부분입니다. 
사용자 관점에서 부드러운 경험을 제공하고, 필요할 때 확장 가능한 애플리케이션을 만드는 것이 중요합니다.
코틀린은 언어 수준에서 코루틴 지원을 제공하고 대부분의 기능을 라이브러리에 위임함으로써 이 문제를 유연하게 해결합니다.
비동기 프로그래밍의 문을 열어줄 뿐만 아니라, 코루틴은 동시성 및 액터와 같은 다양한 가능성을 제공합니다.

```

설명을 보면 코루틴은 비동기 또는 논블로킹 프로그래밍을 할 때 도움을 주는 기능인 것으로 보인다.
코틀린을 사용하는 프레임워크에서는 코루틴 지원을 제공하고 대부분의 기능을 라이브러리에 위임한다고 한다.
라이브러리의 위임???? 안드로이드 Jetpack, 네트워크 통신 라이브러리(Retrofit, okHttp 등)에서 코루틴이 지원된다는 것을 말하는건가???

코루틴이 "기능을 라이브러리에 위임한다"고 설명하는 부분은, 코루틴의 기본적인 동작 원리와 이를 활용하는 다양한 방법들이 코틀린 언어 자체보다는 관련 라이브러리에 의해 제공된다는
의미이다.
이는 코틀린 언어가 코루틴을 지원하기 위한 기본적인 메커니즘을 제공하지만, 실제로 코루틴을 사용하여 비동기 작업을 수행하는 데 필요한 대부분의 기능은 라이브러리를 통해 구현된다는
것을 뜻한다. (아마도?...)

코루틴 기능이 어떻게 라이브러리에 의해 제공되는지 이번 미션에서 IO 작업을 한 Retrofit과 Room을 통해 알아보자.

### Retrofit2에서 Coroutines을 제공한다?

Retrofit2에서 Coroutines을 사용할 때는 아래와 같이 사용할 수 있다.

```kotlin
interface ProductService {
    @GET(ApiClient.Product.GET_PRODUCTS)
    suspend fun getProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): ProductsResponse
}
```

위와 같이 Retrofit2 service interface에 suspend를 붙여주면 Ccoroutines을 사용할 수 있다.

```kotlin

fun loadProducts() = viewModelScope.launch {
    productListPagingSource.load()
}

```

ViewModel에서는 위처럼 코루틴 스코프를 열면 된다.

그런데 여기서 의문이 들 수 있다.
viewModelScope의 dispatchers는 기본적으로 Main인데 네트워크 통신(IO)을 성공적으로 한다는 것이다..!

viewModelScope에서 IO 작업을 하기 위해서는 아래와 같은 코드로 구현해야할 것으로 예상한다.

```kotlin
fun loadProducts() = viewModelScope.launch {
    withContext(Dispatchers.IO) {
        productListPagingSource.load()
    }
}
```

근데 맨 위 코드처럼 별도의 dispatchers를 안붙여도 동작하고, 알아서 UI에서 사용할 수 있도록 만들어준다는 것을 확인할 수 있다.
왜 그런 것일까?! 이유를 생각해보면 Retrofit2 내부적으로 IO 작업을 한다는 것인데, 이게 어떻게 된 것일까?!

위에 코드의 흐름을 나열하면 다음과 같다.

- UI에서 loadProducts()를 호출한다.
- viewModelScope.launch {}를 통해 코루틴을 생성한다. (UI 스케줄러)
- 코루틴 안에서 productListPagingSource.load()를 호출한다. (UI 스케줄러)
- Retrofit2 service에 있는 suspend fun getProducts()가 실행된다.(UI 스케줄러 -> IO 스케줄러)
- Retrofit2 내부적으로 IO를 처리하고, 이를 리턴한다. (IO 스케줄러 -> UI 스케줄러)

결국 Retrofit2에서 Ccoroutines을 사용하면 Retrofit2 내부에서 새로운 스레드를 생성하여 IO 작업을 하고, 작업이 끝나면 UI 스케줄러로 바꿔준다는 것이다.


(작성중... 용어 정리 필요...)

그렇다면 Retrofit2에서 Ccoroutines을 지원해주는 KotlinExtensions 내부 코드를 확인해보자.
KotlinExtensions.kt에는 3개의 await() 관련 코드를 제공한다. Null을 포함하는 제네릭과 Null을 허용하지 않는 제네릭 모두 포함되어 있는데, 방법은 모두
동일하니 이중 하나만 살펴보자.

```kotlin

suspend fun <T : Any> Call<T>.await(): T {
    return suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation {
            cancel()
        }
        enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body == null) {
                        val invocation = call.request().tag(Invocation::class.java)!!
                        val method = invocation.method()
                        val e = KotlinNullPointerException(
                            "Response from " +
                                    method.declaringClass.name +
                                    '.' +
                                    method.name +
                                    " was null but response body type was declared as non-null"
                        )
                        continuation.resumeWithException(e)
                    } else {
                        continuation.resume(body)
                    }
                } else {
                    continuation.resumeWithException(HttpException(response))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })
    }
}

```


Retrofit2에 포함된 kotlin 관련 코드를 보기 전에
KotlinExtensions.kt를 살펴보면 이유를 알 수 있다.

하나 더 알아두면 좋은 것이 suspendCancellableCoroutine이다.

Retroift2의 enqueue와 suspendCancellableCoroutine을 합쳐 만들었다.

원리는 간단한데, suspendCancellableCoroutine는 어떠한 응답이 오기 전까지 코루틴을 대기시킨다. 이 방법을 활용하면 RxJava든, 별도의 Thread든
원하는 응답을 코루틴으로 바꿔치기할 수 있다.

과거에 작성했던 글에서도 볼 수 있는데, RxJava와 Coroutine 함께 사용하기라는 삽질?에서 확인할 수 있다.

참고로 이 suspendCancellableCoroutine을 사용하면 필수로 invokeOnCancellation을 작성해야 한다.



매우 긴 것처럼 보이지만 핵심 코드는 아래와 같다.

suspendCancellableCoroutine을 사용하고 있다.
Retrofit2의 enqueue를 활용하고 있다.
Retrofit2의 enqueue는 어떠한 네트워크 처리 후 이에 대한 응답을 UI에서 바로 사용할 수 있는 형태로 자동으로 변경해 준다. 결국 위 코드의 onResponse,
onFailure으로 들어온 값을 suspendCancellableCoroutine의 응답을 통해 넘겨주는데, 자연스럽게 IO에서 네트워크 처리 후 UI에서 처리할 수 있는 값으로
넘겨준다.

그러니 별도의 withContext를 이용해 Dispatchers.IO와 같은 스케줄러를 지정해 줄 필요성이 없는 것이다.

그럼 언제 withContext(Dispatchers.IO)를 지정할까?
일반적인 경우엔 IO를 지정할 필요성이 없다. IO -> UI -> IO로 돌려주는 과정을 하기 때문에 일반적인 케이스에서는 불필요하다.

하지만 Room을 이용한 백업을 하거나, 데이터 캐싱을 하거나, 처리해야 할 데이터의 양이 많을 경우 UI보다는 IO가 좋다. 이러한 경우라면 IO로 한 번 더 감싸는 건 문제가
없다.

정말 단순하게 사용하는 경우라면 딱히 필요치는 않으니 굳이 IO 스케줄러로 변경할 필요성은 없다.

`Retrofit`과 `kotlinx.coroutines`를 사용하여 네트워크 요청을 비동기로 처리하는 예시입니다.

이 경우에도 Retrofit 라이브러리가 코루틴과의 통합을 통해 비동기 네트워크 요청을 쉽게 처리할 수 있도록 해줍니다.

이 예시에서 `suspend` 함수는 코루틴 내에서 네트워크 요청을 수행합니다. Retrofit은 `suspend` 함수를 지원하여 코루틴을 사용한 비동기 네트워크 호출을 간단하게
만들어줍니다.

이와 같이 코틀린은 코루틴을 통한 비동기 프로그래밍의 기본 메커니즘을 언어 차원에서 제공하지만, 실제로 이를 활용하는 다양한 기능은 관련 라이브러리에 의해 구현됩니다.







### Coroutines + Room

Room 데이터베이스와 코루틴을 사용하여 데이터베이스 작업을 비동기로 처리하는 예시입니다.

1. **Gradle 의존성 추가**:
   ```kotlin
   implementation 'androidx.room:room-runtime:2.4.1'
   implementation 'androidx.room:room-ktx:2.4.1'
   kapt 'androidx.room:room-compiler:2.4.1'
   implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1'
   ```

2. **Room DAO 정의**:
   ```kotlin
   @Dao
   interface UserDao {
       @Query("SELECT * FROM user WHERE id = :userId")
       suspend fun getUser(userId: Int): User

       @Insert
       suspend fun insertUser(user: User)
   }
   ```

3. **코루틴을 이용한 데이터베이스 작업**:
   ```kotlin
   import androidx.room.Room
   import kotlinx.coroutines.*

   fun main() = runBlocking {
       val db = Room.databaseBuilder(
           context,
           AppDatabase::class.java, "database-name"
       ).build()

       val userDao = db.userDao()

       launch {
           val user = userDao.getUser(1)
           println(user)
       }

       launch {
           val newUser = User(id = 1, name = "John Doe")
           userDao.insertUser(newUser)
       }
   }
   ```

이 예시에서는 Room 데이터베이스와 코루틴을 사용하여 데이터베이스 작업을 비동기로 처리합니다. Room 라이브러리는 코루틴과 통합되어 `suspend` 함수를 통해 비동기
데이터베이스 작업을 쉽게 수행할 수 있습니다.

이러한 예시들은 코틀린의 코루틴 기능이 어떻게 라이브러리를 통해 제공되고 활용되는지를 보여줍니다. 코루틴은 비동기 작업을 간단하고 직관적으로 처리할 수 있도록 도와주며, 다양한
라이브러리와의 통합을 통해 그 기능을 더욱 확장할 수 있습니다.

### 예시: 코루틴의 기본적인 사용

코틀린 표준 라이브러리에서 제공하는 `kotlinx.coroutines` 라이브러리를 사용하는 예시이다.

1. **Gradle 의존성 추가**:
   ```kotlin
   implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1'
   ```

2. **코루틴 사용**:
   ```kotlin
   import kotlinx.coroutines.*

   fun main() = runBlocking {
       launch {
           delay(1000L)
           println("World!")
       }
       println("Hello,")
   }
   ```

위 예시에서 `runBlocking`, `launch`, `delay` 함수들은 모두 `kotlinx.coroutines` 라이브러리에 의해 제공됩니다. 코틀린 언어 자체에는
이러한 비동기 함수가 포함되어 있지 않으며, 대신 이 라이브러리가 이러한 기능을 제공합니다.

### 주요 라이브러리 기능:

- **runBlocking**: 코루틴 빌더 중 하나로, 현재 스레드를 차단하여 코루틴이 완료될 때까지 기다립니다.
- **launch**: 새로운 코루틴을 시작하는 빌더로, 비동기 작업을 수행합니다.
- **delay**: 지정된 시간만큼 코루틴을 일시 중단시키는 함수로, 스레드를 차단하지 않고도 지연을 처리할 수 있습니다.

이와 같이 코틀린은 언어 차원에서 코루틴을 지원하고 있지만, 실질적인 비동기 작업, 동시성 제어, 지연 등의 기능은 `kotlinx.coroutines`와 같은 라이브러리에서
제공합니다.

아무튼 코루틴은 비동기 프로그래밍의 문을 열어줄 뿐만 아니라, 동시성 및 액터와 같은 다양한 기능성을 제공한다고 한다.

### 안드류이드에서 코루틴이란?

[안드로이드 공식문서](https://kotlinlang.org/docs/coroutines-overview.html)에서는 코루틴을 다음과 같이 설명한다.

```
코루틴은 비동기로 실행되는 코드를 단순화하기 위해 안드로이드에서 사용할 수 있는 동시성 디자인 패턴입니다. 
코루틴은 Kotlin 1.3 버전에서 추가되었으며, 다른 언어에서 이미 확립된 개념을 기반으로 합니다.
안드로이드에서 코루틴은 메인 스레드를 차단하고 앱이 응답하지 않게 만들 수 있는 장시간 실행 작업을 관리하는 데 도움을 줍니다. 
코루틴을 사용하는 전문 개발자의 50% 이상이 생산성이 향상되었다고 보고했습니다. 
이 주제는 코틀린 코루틴을 사용하여 이러한 문제를 해결하고, 더 깔끔하고 간결한 앱 코드를 작성하는 방법을 설명합니다.

코루틴은 안드로이드에서 비동기 프로그래밍을 위한 권장 솔루션입니다. 주목할 만한 기능은 다음과 같습니다
1. 경량성: 코루틴은 중단 지원 덕분에 여러 코루틴을 단일 스레드에서 실행할 수 있습니다. suspend는 코루틴이 실행되는 스레드를 차단하지 않으므로, 메모리를 절약하면서 많은 동시 작업을 지원합니다.
2. 메모리 누수 감소: 구조화된 동시성을 사용하여 범위 내에서 작업을 실행할 수 있습니다.
3. 내장된 취소 지원: 실행 중인 코루틴 계층을 통해 취소가 자동으로 전파됩니다.
4. Jetpack 통합: 많은 Jetpack 라이브러리는 코루틴을 완벽하게 지원하는 확장을 포함하고 있습니다. 일부 라이브러리는 구조화된 동시성을 위해 사용할 수 있는 자체 코루틴 범위를 제공합니다.
```

### 코루틴은 동시성 보장을 어떻게 할까?

### 코루틴 예외 전파

https://co-zi.medium.com/coroutine-%EC%97%90%EC%84%9C%EC%9D%98-error-handling-fb3a88dcd358

### 코루틴을 지원하는 jetpack 라이브러리는 뭐가 있을까? (그 외 라이브러리도!!!)

### room ktx ???

https://developer.android.com/kotlin/ktx#room
error: To use Coroutine features, you must add `ktx` artifact from Room as a dependency.
androidx.room:room-ktx:<version> public abstract java.lang.Object insertProductHistory(
@org.jetbrains.annotations.NotNull
