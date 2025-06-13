package woowacourse.shopping.data.carts.repository

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.carts.dto.CartItemRequest
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.data.util.AppInterceptor
import woowacourse.shopping.data.util.RetrofitService
import woowacourse.shopping.domain.model.Authorization
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.carts.CartFetchError

class CartRemoteDataSourceImpl(
    baseUrl: String = BuildConfig.BASE_URL,
) : CartRemoteDataSource {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AppInterceptor())
        .build()

    private val retrofitService: RetrofitService =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(RetrofitService::class.java)

    override suspend fun fetchCartItemByPage(
        page: Int,
        size: Int
    ): CartResponse = withContext(Dispatchers.IO) {

        try {
            retrofitService.requestCartProduct(
                page = page,
                size = size,
            )
        } catch (e: Exception) {
            throw CartFetchError.Network
        }
    }

    override suspend fun fetchCartItemByOffset(
        limit: Int,
        offset: Int
    ): CartResponse = fetchCartItemByPage(offset / limit, limit)

    override suspend fun fetchCartCount(): Int = withContext(Dispatchers.IO) {
        try {
            retrofitService
                .requestCartCounts()
                .quantity
        } catch (e: Exception) {
            throw CartFetchError.Network
        }
    }

    override suspend fun fetchAuthCode(validKey: String): Int = withContext(Dispatchers.IO) {
        try {
            retrofitService
            .requestCartCounts().quantity
        } catch (e: Exception) {
            throw CartFetchError.Network
        }
    }

    override suspend fun updateCartItemCount(
        cartId: Int,
        cartQuantity: CartQuantity
    ) = withContext(Dispatchers.IO) {
        try {
            retrofitService.updateCartCounts(
                cartId = cartId,
                requestBody = cartQuantity,
            )
        } catch (e: Exception) {
            throw CartFetchError.Network
        }
    }

    override suspend fun deleteItem(cartId: Int) = withContext(Dispatchers.IO) {
        try {
            retrofitService.deleteCartItem(
                cartId = cartId,
            )
        } catch (e: Exception) {
            throw CartFetchError.Network
        }
    }

    override suspend fun addItem(
        itemId: Int,
        itemCount: Int
    ) = withContext(Dispatchers.IO) {
        try {
            retrofitService.addCartItem(
                cartItem = CartItemRequest(itemId, itemCount),
            )
        } catch (e: Exception) {
            throw CartFetchError.Network
        }
    }
}