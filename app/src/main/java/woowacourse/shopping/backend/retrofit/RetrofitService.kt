package woowacourse.shopping.backend.retrofit

import android.util.Log
import kotlinx.coroutines.CancellationException
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.backend.retrofit.api.CouponRetrofit
import woowacourse.shopping.backend.retrofit.api.OrderRetrofit
import woowacourse.shopping.backend.retrofit.api.ProductRetrofit
import woowacourse.shopping.backend.retrofit.api.ShoppingCartRetrofit
import woowacourse.shopping.backend.retrofit.dto.CartQuantity
import woowacourse.shopping.backend.retrofit.dto.CartRequest
import woowacourse.shopping.backend.retrofit.dto.OrderInfo
import woowacourse.shopping.backend.retrofit.dto.ShoppingCartResponse
import woowacourse.shopping.backend.retrofit.dto.coupon.CouponResponse
import woowacourse.shopping.backend.retrofit.dto.productlist.Product
import woowacourse.shopping.backend.retrofit.dto.productlist.ProductResponse
import woowacourse.shopping.repository.AuthHeaderProvider
import woowacourse.shopping.repository.AuthInterceptor
import java.io.IOException

class RetrofitService(
    authHeaderProvider: AuthHeaderProvider,
    baseUrl: String = BASE_URL,
    mockBaseUrl: String? = null,
) {
    private val client =
        OkHttpClient
            .Builder()
            .addInterceptor(AuthInterceptor(authHeaderProvider))
            .build()

    private val remoteRetrofit =
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private val mockRetrofit =
        mockBaseUrl?.let { fallbackBaseUrl ->
            Retrofit
                .Builder()
                .baseUrl(fallbackBaseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

    private val remoteOrderApiService: OrderRetrofit =
        remoteRetrofit.create(OrderRetrofit::class.java)
    private val remoteProductApiService: ProductRetrofit =
        remoteRetrofit.create(ProductRetrofit::class.java)
    private val remoteShoppingCartApiService: ShoppingCartRetrofit =
        remoteRetrofit.create(ShoppingCartRetrofit::class.java)
    private val remoteCouponApiService: CouponRetrofit =
        remoteRetrofit.create(CouponRetrofit::class.java)

    private val mockOrderApiService: OrderRetrofit? =
        mockRetrofit?.create(OrderRetrofit::class.java)
    private val mockProductApiService: ProductRetrofit? =
        mockRetrofit?.create(ProductRetrofit::class.java)
    private val mockShoppingCartApiService: ShoppingCartRetrofit? =
        mockRetrofit?.create(ShoppingCartRetrofit::class.java)
    private val mockCouponApiService: CouponRetrofit? =
        mockRetrofit?.create(CouponRetrofit::class.java)

    val orderApiService: OrderRetrofit =
        createOrderApiService()
    val productApiService: ProductRetrofit =
        createProductApiService()
    val shoppingCartApiService: ShoppingCartRetrofit =
        createShoppingCartApiService()
    val couponApiService: CouponRetrofit =
        createCouponApiService()

    private fun createProductApiService(): ProductRetrofit {
        val fallbackApi = mockProductApiService ?: return remoteProductApiService

        return object : ProductRetrofit {
            override suspend fun requestProducts(
                accept: String,
                page: Int,
                size: Int,
                sort: List<String>?,
                category: String?,
            ): Response<ProductResponse> =
                callWithFallback(
                    endpoint = "GET /products",
                    remoteCall = {
                        remoteProductApiService.requestProducts(
                            accept = accept,
                            page = page,
                            size = size,
                            sort = sort,
                            category = category,
                        )
                    },
                    mockCall = {
                        fallbackApi.requestProducts(
                            accept = accept,
                            page = page,
                            size = size,
                            sort = sort,
                            category = category,
                        )
                    },
                )

            override suspend fun requestProductDetail(
                accept: String,
                id: Long,
            ): Response<Product> =
                callWithFallback(
                    endpoint = "GET /products/$id",
                    remoteCall = { remoteProductApiService.requestProductDetail(accept = accept, id = id) },
                    mockCall = { fallbackApi.requestProductDetail(accept = accept, id = id) },
                )

            override suspend fun addProduct(
                accept: String,
                product: Product,
            ): Response<Unit> =
                callWithFallback(
                    endpoint = "POST /products",
                    remoteCall = { remoteProductApiService.addProduct(accept = accept, product = product) },
                    mockCall = { fallbackApi.addProduct(accept = accept, product = product) },
                )

            override suspend fun deleteProduct(
                accept: String,
                id: Long,
            ): Response<Unit> =
                callWithFallback(
                    endpoint = "DELETE /products/$id",
                    remoteCall = { remoteProductApiService.deleteProduct(accept = accept, id = id) },
                    mockCall = { fallbackApi.deleteProduct(accept = accept, id = id) },
                )
        }
    }

    private fun createShoppingCartApiService(): ShoppingCartRetrofit {
        val fallbackApi = mockShoppingCartApiService ?: return remoteShoppingCartApiService

        return object : ShoppingCartRetrofit {
            override suspend fun requestCartItems(
                accept: String,
                page: Int,
                size: Int,
                sort: List<String>?,
            ): Response<ShoppingCartResponse> =
                callWithFallback(
                    endpoint = "GET /cart-items",
                    remoteCall = {
                        remoteShoppingCartApiService.requestCartItems(
                            accept = accept,
                            page = page,
                            size = size,
                            sort = sort,
                        )
                    },
                    mockCall = {
                        fallbackApi.requestCartItems(
                            accept = accept,
                            page = page,
                            size = size,
                            sort = sort,
                        )
                    },
                )

            override suspend fun addCartItem(
                accept: String,
                product: CartRequest,
            ): Response<Unit> =
                callWithFallback(
                    endpoint = "POST /cart-items",
                    remoteCall = { remoteShoppingCartApiService.addCartItem(accept = accept, product = product) },
                    mockCall = { fallbackApi.addCartItem(accept = accept, product = product) },
                )

            override suspend fun deleteCartItem(
                accept: String,
                id: Int,
            ): Response<Unit> =
                callWithFallback(
                    endpoint = "DELETE /cart-items/$id",
                    remoteCall = { remoteShoppingCartApiService.deleteCartItem(accept = accept, id = id) },
                    mockCall = { fallbackApi.deleteCartItem(accept = accept, id = id) },
                )

            override suspend fun updateQuantityCartItem(
                accept: String,
                id: Int,
                product: CartQuantity,
            ): Response<Unit> =
                callWithFallback(
                    endpoint = "PATCH /cart-items/$id",
                    remoteCall = {
                        remoteShoppingCartApiService.updateQuantityCartItem(
                            accept = accept,
                            id = id,
                            product = product,
                        )
                    },
                    mockCall = {
                        fallbackApi.updateQuantityCartItem(
                            accept = accept,
                            id = id,
                            product = product,
                        )
                    },
                )

            override suspend fun requestQuantityCartItem(accept: String): Response<CartQuantity> =
                callWithFallback(
                    endpoint = "GET /cart-items/counts",
                    remoteCall = { remoteShoppingCartApiService.requestQuantityCartItem(accept = accept) },
                    mockCall = { fallbackApi.requestQuantityCartItem(accept = accept) },
                )
        }
    }

    private fun createOrderApiService(): OrderRetrofit {
        val fallbackApi = mockOrderApiService ?: return remoteOrderApiService

        return object : OrderRetrofit {
            override suspend fun order(
                accept: String,
                order: OrderInfo,
            ): Response<Unit> =
                callWithFallback(
                    endpoint = "POST /orders",
                    remoteCall = { remoteOrderApiService.order(accept = accept, order = order) },
                    mockCall = { fallbackApi.order(accept = accept, order = order) },
                )
        }
    }

    private fun createCouponApiService(): CouponRetrofit {
        val fallbackApi = mockCouponApiService ?: return remoteCouponApiService

        return object : CouponRetrofit {
            override suspend fun getCoupons(accept: String): List<CouponResponse> =
                callWithFallbackValue(
                    endpoint = "GET /coupons",
                    remoteCall = { remoteCouponApiService.getCoupons(accept = accept) },
                    mockCall = { fallbackApi.getCoupons(accept = accept) },
                )
        }
    }

    private suspend fun <T> callWithFallback(
        endpoint: String,
        remoteCall: suspend () -> Response<T>,
        mockCall: suspend () -> Response<T>,
    ): Response<T> =
        try {
            val remoteResponse = remoteCall()
            if (!remoteResponse.isSuccessful) {
                Log.w(
                    TAG,
                    "Remote request failed for $endpoint with HTTP ${remoteResponse.code()}, using mock fallback.",
                )
                mockCall()
            } else {
                Log.d(TAG, "Remote request succeeded for $endpoint.")
                remoteResponse
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: IOException) {
            Log.w(TAG, "Remote request failed for $endpoint with IOException, using mock fallback.", exception)
            mockCall()
        } catch (exception: Exception) {
            Log.w(TAG, "Remote request failed for $endpoint with unexpected exception, using mock fallback.", exception)
            mockCall()
        }

    private suspend fun <T> callWithFallbackValue(
        endpoint: String,
        remoteCall: suspend () -> T,
        mockCall: suspend () -> T,
    ): T =
        try {
            remoteCall().also {
                Log.d(TAG, "Remote request succeeded for $endpoint.")
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: IOException) {
            Log.w(TAG, "Remote request failed for $endpoint with IOException, using mock fallback.", exception)
            mockCall()
        } catch (exception: Exception) {
            Log.w(TAG, "Remote request failed for $endpoint with unexpected exception, using mock fallback.", exception)
            mockCall()
        }

    private companion object {
        private const val TAG = "RetrofitService"
        private const val BASE_URL =
            "http://techcourse-lv2-alb-250216202.ap-northeast-2.elb.amazonaws.com/"
    }
}
