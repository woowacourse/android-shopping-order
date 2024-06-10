> execute와 enqueue 중 왜 execute를 사용하셨고,   
> 어떤 단점이 있으며 어떻게 개선할 수 있을까요?


# Call 문서
> 서버로 req. 보내고, resp. 받는 레트로핏 메서드의 호출이다.  
> 각 call 은 자신의 http req.-resp. 쌍을 실행한다.  
> `clone` 메서드를 사용해서 같은 웹 서버로 같은 파라미터를 가진 여러 call 을 만들 수 있다.  
> 이것은 polling 이나 실패한 호출 재시도를 위해 사용된다.  
> Calls 은 **`excute` 로 synchronously 하게** 실행되거나, **`enqueue` 로 asynchronously 하게** 실행된다.  
> 각 케이스에서 call 은 cancel 로 언제든지 취소될 수 있다.  
> busy writing 중인 call 은 IOException 을 받을 수 있다. (busy writing: 요청을 보내는 중이거나 응답을 받는 중인 상태를 의미한다.)

그런데 execute 를 실행하는 동작을 thread 를 열어서 하면 괜찮지 않나? 

## execute
execute 는 동기적으로 호출을 수행한다. 즉, 호출은 동일한 스레드에서 이루어지며 응답이 수신되거나 오류가 발생할 때까지 스레드가 차단된다.

### 장단점
- 장점
  - 간단하다.
  - 콜백이 필요 없다.
그래서 sequential 논리를 처리하는 시나리오에서 코드가 굉장히 간단하다.  
- 단점
  - Blocking
  - Main Thread 에서 호출하면 ANR 발생 가능성이 있다.

응답을 받을 때까지 현재 스레드를 차단하기 때문에, 네트워크 호출이 오래 걸리면 앱이 응답하지 않는다.  
네트워크 응답이 느린 경우, ANR (Application Not Responding) 이 발생할 수 있다.  

### `thread { .. }.join()` 의 단점

`thread { .. }.join()` 을 사용하면, 별도의 thread 를 열어서 작업을 수행하고, **그 작업이 끝날 때까지 기다리는 것**이다.    
그래서 **실재로 blocking 이 발생할 수 있습니다.**    
**응답속도를 줄이거나, 느린 네트워크를 사용할 때는 앱이 응답하지 않을 수** 있습니다.
이를 해결하기 위해서는 코루틴, RxJava, LiveData 등을 사용할 수 있습니다.

위의 것들을 사용하지 않는다면, enqueue callback 을 사용할 수도 있습니다.


## enqueue
enqueue 는 non-blocking 한 방식으로, 비동기적으로 동작한다. 별도의 스레드에서 이루어지며 메인 스레드는 다른 작업을 수행할 수 있다.

### 장단점
- 장점
  - 비동기적으로 동작하기 때문에, 메인 스레드가 차단되지 않는다.
  - 네트워크 호출이 오래 걸려도 앱이 응답한다.
- 단점
  - 콜백을 사용해야 한다.
  - 성공, 실패를 콜백에서 처리해야 한다. 그래서 코드가 복잡해진다.

만약 enqueue 를 사용한다면?
```kotlin
class CartItemRemoteDataSource(private val cartItemApiService: CartItemApiService) :
    ShoppingCartDataSource {

    fun findByProductIdAsync(productId: Long, callback: (ProductIdsCountData?) -> Unit) {
        cartItemApiService.requestCartItems().enqueue(object : Callback<CartItemResponse> {
            override fun onResponse(call: Call<CartItemResponse>, response: Response<CartItemResponse>) {
                val allCartItems = response.body()?.content ?: run {
                    callback(null)
                    return
                }
                val find = allCartItems.find { it.product.id == productId }
                if (find != null) {
                    callback(ProductIdsCountData(find.product.id, find.quantity))
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<CartItemResponse>, t: Throwable) {
                callback(null) // Handle error case, or log the error
            }
        })
    }
}

```

이런 식으로 사용할 수 있을 것 같아요.  

이렇게 enqueue 를 사용하고 callback 을 viewModel 까지 전파시킨 후에,  
동작을 성공했을 때, 실패했을 때의 로직을 ViewModel 에서 처리하면 될 것 같아요.  

그렇게 되면 **뷰모델에서 별도의 thread 를 열 필요가 없어집니다!!!**  

하지만, 만약 viewModel 에서 웹 서버와 통신하는 로직이 많아지고, 이것이 순차적으로 수행되어야 한다면,  
callback hell 이 만들어 질 수 있습니다..  

예를 들어서 
```kotlin
class AViewModel ( ... ) {
    fun complexOperation{
        // cartItem 을 POST

        // 수정된 cartItem GET (위에서 POST 가 끝난 후에!)
      
        // 수정된 cartItem 데이터를 통해서 다른 데이터 가져오기 (위에서 POST, GET 가 끝난 후에!) 
    }
}
```

이런 식으로 되어 있다고 할 때 모두 enqueue 를 사용한 콜백으로 처리하면,  callback hell 이 만들어질 수 있습니다.   

실제로 제가 이전 미션에서 callback 을 사용해 보았었는데요.  

```kotlin
override fun loadAllProductsAsyncResult(
  page: Int,
  callback: (Result<List<Product>>) -> Unit,
) {
  productsSource.findByPagedAsyncResult(page) { productsResult ->
    productsResult.map { productData ->
      cartSource.loadAllAsyncResult { cartsResult ->
        cartsResult.onSuccess { cartsData ->
          val products =
            productData.map { productData ->
              cartsData.find { cartData ->
                cartData.productId == productData.id
              }?.let {
                productData.toDomain(it.quantity)
              } ?: productData.toDomain(0)
            }
          callback(Result.success(products))
        }
      }
    }
  }
}

```

이런식으로 콜백 지옥이 만들어질 수 있더라구요.. 

그래서 적절한 상황에 맞게 사용하는 것이 중요할 것 같습니다.  
이전 미션에서 콜백 지옥을 경험해보았기 때문에 이번 미션에서는 enqueue 로 바꾸는 과정은 직접 하지 않으려고 합니다.   

