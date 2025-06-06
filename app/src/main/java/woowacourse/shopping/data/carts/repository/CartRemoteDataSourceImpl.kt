package woowacourse.shopping.data.carts.repository

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.carts.CartFetchError
import woowacourse.shopping.data.carts.CartFetchResult
import woowacourse.shopping.data.carts.CartUpdateError
import woowacourse.shopping.data.carts.dto.CartItemRequest
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.data.util.HeaderInterceptor
import woowacourse.shopping.data.util.RetrofitService

class CartRemoteDataSourceImpl(
    baseUrl: String = BuildConfig.BASE_URL,
) : CartRemoteDataSource {
    private val client =
        OkHttpClient
            .Builder()
            .addInterceptor(HeaderInterceptor())
            .build()

    private val retrofitService: RetrofitService =
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(RetrofitService::class.java)

    override suspend fun fetchCartItemByOffset(
        limit: Int,
        offset: Int,
    ): CartFetchResult<CartResponse> = fetchCartItemByPage(offset / limit, limit)

    override suspend fun fetchCartItemByPage(
        page: Int,
        size: Int,
    ): CartFetchResult<CartResponse> =
        try {
            val response = retrofitService.requestCartProduct(page = page, size = size)
            CartFetchResult.Success(response)
        } catch (e: Exception) {
            CartFetchResult.Error(CartFetchError.Network)
        }

    override fun fetchCartCount(
        onSuccess: (Int) -> Unit,
        onFailure: (CartFetchError) -> Unit,
    ) {
        retrofitService
            .requestCartCounts()
            .enqueue(
                object : Callback<CartQuantity> {
                    override fun onResponse(
                        call: Call<CartQuantity>,
                        response: Response<CartQuantity>,
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            onSuccess(response.body()!!.quantity)
                        } else {
                            onFailure(CartFetchError.Server(response.code(), response.message()))
                        }
                    }

                    override fun onFailure(
                        call: Call<CartQuantity>,
                        t: Throwable,
                    ) {
                        onFailure(CartFetchError.Network)
                    }
                },
            )
    }

    override fun fetchAuthCode(
        validKey: String,
        onResponse: (Int) -> Unit,
        onFailure: (CartFetchError) -> Unit,
    ) {
        retrofitService
            .requestCartCounts()
            .enqueue(
                object : Callback<CartQuantity> {
                    override fun onResponse(
                        call: Call<CartQuantity>,
                        response: Response<CartQuantity>,
                    ) {
                        onResponse(response.code())
                    }

                    override fun onFailure(
                        call: Call<CartQuantity>,
                        t: Throwable,
                    ) {
                        onFailure(CartFetchError.Network)
                    }
                },
            )
    }

    override fun updateCartItemCount(
        cartId: Int,
        cartQuantity: CartQuantity,
        onSuccess: (resultCode: Int) -> Unit,
        onFailure: (CartUpdateError) -> Unit,
    ) {
        retrofitService
            .updateCartCounts(
                cartId = cartId,
                requestBody = cartQuantity,
            ).enqueue(
                object : Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit>,
                        response: Response<Unit>,
                    ) {
                        if (response.isSuccessful) {
                            onSuccess(response.code())
                        } else {
                            val errorBody = response.errorBody()?.string() ?: ""
                            when {
                                errorBody.contains("cartItem not found") -> onFailure(CartUpdateError.NotFound)
                                else -> onFailure(CartUpdateError.Server(response.code(), errorBody))
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<Unit>,
                        t: Throwable,
                    ) {
                        onFailure(CartUpdateError.Network)
                    }
                },
            )
    }

    override suspend fun deleteItem(cartId: Int): CartFetchResult<Int> =
        try {
            val response = retrofitService.deleteCartItem(cartId = cartId)
            if (response.isSuccessful) {
                CartFetchResult.Success(response.code())
            } else {
                CartFetchResult.Error(
                    CartFetchError.Server(response.code(), response.message()),
                )
            }
        } catch (e: Exception) {
            CartFetchResult.Error(CartFetchError.Network)
        }

    override fun addItem(
        itemId: Int,
        itemCount: Int,
        onSuccess: (resultCode: Int, cartId: Int) -> Unit,
        onFailure: (CartFetchError) -> Unit,
    ) {
        retrofitService
            .addCartItem(
                cartItem = CartItemRequest(itemId, itemCount),
            ).enqueue(
                object : Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit>,
                        response: Response<Unit>,
                    ) {
                        if (response.isSuccessful) {
                            val location = response.headers()["Location"]
                            val cartItemId = extractCartItemId(location)

                            onSuccess(response.code(), cartItemId)
                        } else {
                            onFailure(CartFetchError.Server(response.code(), response.message()))
                        }
                    }

                    override fun onFailure(
                        call: Call<Unit>,
                        t: Throwable,
                    ) {
                        onFailure(CartFetchError.Network)
                    }
                },
            )
    }

    private fun extractCartItemId(location: String?): Int =
        try {
            location?.substringAfterLast("/")?.toInt() ?: 0
        } catch (e: NumberFormatException) {
            0
        }
}
