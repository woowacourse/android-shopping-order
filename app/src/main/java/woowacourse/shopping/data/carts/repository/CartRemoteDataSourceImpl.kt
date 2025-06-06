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
import woowacourse.shopping.data.carts.AddItemResult
import woowacourse.shopping.data.carts.CartFetchError
import woowacourse.shopping.data.carts.CartFetchResult
import woowacourse.shopping.data.carts.CartUpdateError
import woowacourse.shopping.data.carts.CartUpdateResult
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

    override suspend fun updateCartItemCount(
        cartId: Int,
        cartQuantity: CartQuantity,
    ): CartUpdateResult<Int> {
        val response =
            retrofitService.updateCartCounts(
                cartId = cartId,
                requestBody = cartQuantity,
            )
        return if (response.isSuccessful) {
            CartUpdateResult.Success(response.code())
        } else {
            if (response.code() == 400) {
                CartUpdateResult.Error(CartUpdateError.NotFound)
            } else {
                CartUpdateResult.Error(
                    CartUpdateError.Server(response.code(), response.message()),
                )
            }
        }
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

    override suspend fun addItem(
        itemId: Int,
        itemCount: Int,
    ): CartFetchResult<AddItemResult> {
        try {
            val response =
                retrofitService
                    .addCartItem(
                        cartItem = CartItemRequest(itemId, itemCount),
                    )
            if (response.isSuccessful) {
                val location = response.headers()["Location"]
                val cartItemId = extractCartItemId(location)

                return CartFetchResult.Success(AddItemResult(response.code(), cartItemId))
            } else {
                return CartFetchResult.Error(
                    CartFetchError.Server(response.code(), response.message()),
                )
            }
        } catch (e: Exception) {
            return CartFetchResult.Error(CartFetchError.Network)
        }
    }

    private fun extractCartItemId(location: String?): Int =
        try {
            location?.substringAfterLast("/")?.toInt() ?: 0
        } catch (e: NumberFormatException) {
            0
        }
}
