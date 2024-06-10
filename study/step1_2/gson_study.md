# 직렬화 라이브러리로 GSON 을 선택한 이유

JSON 직렬화/역직렬화를 위해 GSON 라이브러리를 선택했습니다.
[GSON 라이브러리](https://github.com/google/gson)는 구글에 의해 개발된 JSON 직렬화/역직렬화 라이브러리입니다.

- 직렬화/역직렬화를 `toJson()` 혹은 `fromJSON()` 메서드를 통해서 간단히 변환할 수 있습니다.
- 수정 불가능한 기존 객체를 JSON 으로 변환하는 것이 가능합니다.
- java generics 의 지원이 가능합니다.
- JSON 데이터를 Java 객체로 변환할 때, 필드 이름이 다른 경우에도 매핑이 가능합니다.  (`@SerializedName` 사용)

이 프로젝트에서는 서버 통신을 위해 Retrofit 라이브러리를 사용하고 있습니다.  
Retrofit 라이브러리를 사용할 때 직렬화에 Gson 으로 사용하는 게 가장 편리하다고 판단했습니다.

### GSON 과 Retrofit 의 연동

Retrofit 은 `GsonConverterFactory` 를 통해서 GSON 에 대한 지원을 제공합니다.  
이 편하고 강력한 매핑 기능을 사용해서 JSON 형태의 Response 를 java/kotlin 객체로 변환하는 것을 자동으로 처리할 수 있습니다.

```kotlin
val retrofitService: Retrofit =
    Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
```

이런 식으로 간단하게 할 수 있습니다!

### 쉬운 사용

- JSON 을 데이터 객체에 자동으로 매핑하고, biolerplate 코드를 줄일 수 있습니다. 그래서 네트워크 통신 게층 코드를 간결하게 작성할 수 있습니다.

```kotlin
interface CartItemApiService {
    @GET("/cart-items")
    fun requestCartItems(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
    ): Call<CartItemResponse>
}
```

이렇게 작성하면, 이를 사용하는 클라이언트 코드에서는 단순히

```kotlin
class CartItemRemoteDataSource(private val cartItemApiService: CartItemApiService) :
    ShoppingCartDataSource {
    override fun findByProductId(productId: Long): ProductIdsCountData? {
        val allCartItems = cartItemApiService.requestCartItems()
        // ...
    }
}
```

이런 식으로 사용하면 됩니다!

### 쉬운 커스텀

GSON 을 사용하면 직렬/역직렬 변환기를 커스텀해서 사용하기 편합니다.

예를 들어서 Date 타입을 JSON 으로 변환할 때, 다양한 포맷을 지원하고 싶다면 아래와 같이 커스텀할 수 있습니다.  
[참고](https://medium.com/@haohcraft/deserialize-and-serialize-datetime-with-gson-82ea59e874c7)

```kotlin
object MyDateTypeAdapter : JsonDeserializer<Date>, JsonSerializer<Date> {

    private val DATE_FORMATS = arrayOf("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd")
    private val dateFormatters: List<SimpleDateFormat> = DATE_FORMATS.map { SimpleDateFormat(it, Locale.US) }
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date {
        for (formatter in dateFormatters) {
            try {
                return formatter.parse(json?.asString)
            } catch (ignore: ParseException) {
            }
        }

        throw JsonParseException("DateParseException: " + json.toString())
    }

    override fun serialize(src: Date?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        synchronized(designatedFormat) {
            val dateFormatAsString: String = designatedFormat.format(src)
            return JsonPrimitive(dateFormatAsString)
        }
    }
}

val gson = GsonBuilder().apply {
    registerTypeAdapter(Date::class.java, MyDateTypeAdapter)
}.create()

// ...

val retrofitService: Retrofit =
    Retrofit.Builder()
        .baseUrl("BASE_URL")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
///... 
```

### 가벼움

상대적으로 리소스가 적은 모바일 앱에서 사용하기에 가벼운 편입니다.  
Spring 프레임워크에서 기본으로 제공하는 Jackson 라이브러리에 비해서 더 가벼운 편입니다.

그런데 이번에 공부하면서 Square 에서 만든 Moshi 라이브러리를 알게 되었습니다.  
[Moshi 가 더 빠른 성능을 보여준다는 글](https://www.ericthecoder.com/2020/09/29/gson-vs-jackson-vs-moshi-the-best-android-json-parser/)
도 있네요..

1번 (Parsing time in milliseconds) 진행했을 때

|       | Short | Medium | Long |
|-------|-------|--------|------|
| GSON  | 6     | 14     | 35   |
| Moshi | 8     | 11     | 24   |

1,000번 (Parsing time in milliseconds) 진행했을 때

| Short | Medium | Long |
|-------|--------|------|
| 0.53  | 0.81   | 4.20 |
| 0.38  | 0.61   | 3.57 |

5,000 번 (Parsing time in milliseconds) 진행했을 때

|       | Short | Medium | Long  |
|-------|-------|--------|-------|
| GSON  | 2.28  | 2.85   | 17.83 |
| Moshi | 1.59  | 2.68   | 19.69 |

#### 이 부분은 몰랐는데 Retrofit 을 만든 Square 에서 만든 직렬화/역직렬화 라이브러리인 만큼 다음에 기회가 되면 Moshi 라이브러리도 사용해봐야 겠네요.

### 많이 사용해서(?)

조금 이상한 이유일 수 있습니다. ㅎ   
널리 채택된 라이브러리라는 것은 그만큼 커뮤니티 지원과 레퍼런스가 많다는 것을 의미합니다.  
또, 업데이트도 안정적으로 이루어지고 있다는 것을 의미합니다.  
이것도 꽤나 중요한 이유라고 생각합니다.

### 구글이 지원함.

GSON 은 구글에서 개발된 것인 만큼, 안정적이고 믿을 수 있습니다.  
또한 다른 구글 라이브러리와의 호환성도 좋을 것입니다.


# 데이터를 매핑해주는 어댑터?

> GsonConverter를 사용하면서 response객체들을 생성해주기만 하면 받아올 수 있었는데요
> 데이터를 mapping해주는 코드들(adapter)은 언제 어떻게 생성될까요?

### 언제?
Gson 어댑터가 생성되는 시점은 레트로핏-Gson 이 직렬화/역직렬화할 필요가 있을 때 런타임에 생성됩니다.  
두 가지 시점에서 일어납니다.  

1. 레트로핏이 초기화될 때  
GsonConverterFactory 를 레트로핏에 추가할 때 Gson 인스턴스를 생성합니다.  
Retrofit 초기화에는 Converter 설정이 일어나지만, 실제로 어댑터가 생성되는 시점은 네트워크 요청을 만들 때입니다.  
lazy loading 이라고 생각하면 될 것 같습니다.  

2. 네트워크 요청을 만들 때
실제로 어댑터가 생성되는 시점입니다.  
이 시점에서 Gson 은 레트로핏 인터페이스 메소드 (`CartItemApiService` 의 `requestCartItems()` 같은)   
에 지정된 response 타입을 검사하고, 적절한 어댑터를 동적으로 생성하거나, 검색합니다.  

여기에서 여러 어댑터가 생성될 수 있습니다.  
만약 여러 타입의 response 가 있을 경우, 각 타입에 대한 어댑터가 생성됩니다.  
그리고 싱글톤 레지스트리에 캐싱해두고, 이후에 같은 타입의 response 가 있을 때는 캐싱된 어댑터를 사용합니다.  


### 어떻게?

Gson 은 리플렉션을 사용해서 Java/Kotlin 클래스를 검사하고, 런타임에 타입 어댑터를 생성합니다.  
이 어댑터가 JSON 데이터를 클래스 객체로 변환해주는 역할을 합니다.  
GsonConverterFactory 의 코드를 보면

```java
public final class GsonConverterFactory extends Converter.Factory {

    private final Gson gson;

    // ...
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(
            Type type, Annotation[] annotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonResponseBodyConverter<>(gson, adapter);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(
            Type type,
            Annotation[] parameterAnnotations,
            Annotation[] methodAnnotations,
            Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonRequestBodyConverter<>(gson, adapter);
    }
}

```

`responseBodyConverter`, `requestBodyConverter` 에서 gson 이라는 인스턴스의 변수에 접근해서 `getAdapter` 를 통해 타입 어댑터를 가져옵니다.  
그리고 이 어댑터를 통해서 변환을 수행합니다.  

여기서 TypeToken 은 Gson 에서 제공하는 클래스로, Type 을 래핑하고 있습니다.  
코틀린/자바는 타입 소거가 일어나기 때문에, 런타임에 타입 정보를 얻기 위해서 사용합니다.  
이를 통해서 Gson 은 런타임에 타입 정보를 얻어서 변환을 수행합니다.  

제가 과거에 애노테이션과 리플렉션을 공부할 때 코인액 책을 봤었습니다.  
이 때 [JKid 라는 직렬화/역직렬화 라이브러리를 뜯어보면서 공부했었는데](https://sh1mj1-log.tistory.com/235) JKid 는 리플렉션을 사용해서 어노테이션을 분석하고, 직렬화/역직렬화를 수행합니다.  
Gson 은 JKid 와 비슷한 방식으로 동작하는 것 같네요.  
다만, Gson 은 TypeToken 이라는 클래스를 통해서 타입 정보를 래핑하고 있습니다.  

어댑터가 이러한 방식으로 생성되기 때문에 [위에서 언급한 것처럼, 커스텀 변환기를 만들기 쉽습니다](#쉬운-커스텀)! 
