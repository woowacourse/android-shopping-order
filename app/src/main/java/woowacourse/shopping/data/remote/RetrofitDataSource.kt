package woowacourse.shopping.data.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.data.remote.dto.request.ProductRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.data.remote.dto.response.CartResponse
import woowacourse.shopping.data.remote.dto.response.Product
import woowacourse.shopping.data.remote.dto.response.ProductResponse
import woowacourse.shopping.data.remote.dto.response.QuantityResponse
import woowacourse.shopping.data.remote.service.CartItemApi
import woowacourse.shopping.data.remote.service.OrderApi
import woowacourse.shopping.data.remote.service.ProductApi

class RetrofitDataSource(
    private val productApi: ProductApi = RetrofitModule.productApi,
    private val cartItemApi: CartItemApi = RetrofitModule.cartItemsApi,
    private val orderApi: OrderApi = RetrofitModule.orderApi,
) : RemoteDataSource {
    override fun getProducts(
        category: String?,
        page: Int,
        size: Int,
        callback: (Result<ProductResponse>) -> Unit
    ) {
        productApi.getProducts(category = category, page = page, size = size)
            .enqueue(object : Callback<ProductResponse> {
                override fun onResponse(
                    call: Call<ProductResponse>,
                    response: Response<ProductResponse>,
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { callback(Result.success(it)) }
                    } else {
                        callback(Result.failure(Exception("Error: ${response.code()}")))
                    }
                }

                override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }

    override fun addProduct(productRequest: ProductRequest): Response<Unit> {
        return productApi.addProduct(productRequest = productRequest).execute()
    }

    override fun getProductById(id: Int): Response<Product> {
        return productApi.getProductById(id = id).execute()
    }

    override fun deleteProductById(id: Int): Response<Unit> {
        return productApi.deleteProductById(id = id).execute()
    }

    override fun getCartItems(
        page: Int,
        size: Int,
        callback: (Result<CartResponse>) -> Unit,
    ) {
        cartItemApi.getCartItems(page = page, size = size).enqueue(object : Callback<CartResponse> {
            override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(Result.success(it)) }
                } else {
                    callback(Result.failure(Exception("Error: ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    override fun postCartItem(cartItemRequest: CartItemRequest): Response<Unit> {
        return cartItemApi.postCartItem(cartItemRequest = cartItemRequest).execute()
    }

    override fun deleteCartItem(id: Int): Response<Unit> {
        return cartItemApi.deleteCartItem(id = id).execute()
    }

    override fun patchCartItem(
        id: Int,
        quantityRequest: QuantityRequest,
    ): Response<Unit> {
        return cartItemApi.patchCartItem(id = id, quantityRequest = quantityRequest).execute()
    }

    override fun getCartItemsCounts(callback: (Result<QuantityResponse>) -> Unit) {
        cartItemApi.getCartItemsCounts().enqueue(object : Callback<QuantityResponse> {
            override fun onResponse(
                call: Call<QuantityResponse>,
                response: Response<QuantityResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(Result.success(it)) }
                } else {
                    callback(Result.failure(Exception("Error: ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<QuantityResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    override fun postOrders(orderRequest: OrderRequest): Response<Unit> {
        return orderApi.submitOrders(orderRequest = orderRequest).execute()
    }
}
