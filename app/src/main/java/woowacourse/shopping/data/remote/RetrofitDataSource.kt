package woowacourse.shopping.data.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.data.remote.dto.request.ProductRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.data.remote.dto.response.Cart
import woowacourse.shopping.data.remote.dto.response.CartResponse
import woowacourse.shopping.data.remote.dto.response.Product
import woowacourse.shopping.data.remote.dto.response.ProductResponse
import woowacourse.shopping.data.remote.dto.response.QuantityResponse
import woowacourse.shopping.data.remote.service.CartItemApi
import woowacourse.shopping.data.remote.service.OrderApi
import woowacourse.shopping.data.remote.service.ProductApi
import woowacourse.shopping.domain.CartProduct

class RetrofitDataSource(
    private val productApi: ProductApi = RetrofitModule.productApi,
    private val cartItemApi: CartItemApi = RetrofitModule.cartItemsApi,
    private val orderApi: OrderApi = RetrofitModule.orderApi,
) : RemoteDataSource {
    override suspend fun getProducts(
        category: String?,
        page: Int,
        size: Int,
    ): Result<List<Product>> = runCatching {
        productApi.getProducts(category = category, page = page, size = size).content
    }

    override suspend fun addProduct(productRequest: ProductRequest) {
        productApi.addProduct(productRequest = productRequest)
    }

    override suspend fun getProductById(id: Int): Result<Product> = runCatching {
        productApi.getProductById(id = id)
    }

    override suspend fun deleteProductById(id: Int) {
        productApi.deleteProductById(id = id)
    }

    override suspend fun getCartItems(
        page: Int,
        size: Int,
    ): Result<List<Cart>> = runCatching {
        cartItemApi.getCartItems(page = page, size = size).content
    }

    // todo 함수명 수정
    override suspend fun postCartItem(cartItemRequest: CartItemRequest): Result<Response<Unit>> =
        runCatching {
            cartItemApi.postCartItem(cartItemRequest = cartItemRequest)
        }

    override suspend fun deleteCartItem(id: Int): Result<Unit> = runCatching {
        cartItemApi.deleteCartItem(id = id)
    }

    override suspend fun patchCartItem(
        id: Int,
        quantityRequest: QuantityRequest,
    ): Result<Unit> = runCatching {
        cartItemApi.patchCartItem(id = id, quantityRequest = quantityRequest)
    }

    override suspend fun getCartItemsCounts(): Result<QuantityResponse> = runCatching {
        cartItemApi.getCartItemsCounts()
    }

    /*        cartItemApi.getCartItemsCounts().enqueue(
                object : Callback<QuantityResponse> {
                    override fun onResponse(
                        call: Call<QuantityResponse>,
                        response: Response<QuantityResponse>,
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let { callback(Result.success(it)) }
                        } else {
                            callback(Result.failure(Exception("Error: ${response.code()}")))
                        }
                    }

                    override fun onFailure(
                        call: Call<QuantityResponse>,
                        t: Throwable,
                    ) {
                        callback(Result.failure(t))
                    }
                },
            )*/

    override suspend fun postOrders(orderRequest: OrderRequest): Result<Unit> = runCatching {
        orderApi.submitOrders(orderRequest = orderRequest)
    }
}
