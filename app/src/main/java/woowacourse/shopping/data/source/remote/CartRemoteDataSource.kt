package woowacourse.shopping.data.source.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okio.IOException
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import woowacourse.shopping.data.source.remote.api.AddItemRequestBody
import woowacourse.shopping.data.source.remote.api.CartService
import woowacourse.shopping.data.source.remote.dto.cart.CartContent
import woowacourse.shopping.di.RepositoryProvider
import woowacourse.shopping.di.RepositoryProvider.authRepository
import woowacourse.shopping.domain.repository.AuthRepository
import kotlin.jvm.java

class CartRemoteDataSource(
    private val authRepository: AuthRepository = RepositoryProvider.authRepository,
    private val baseUrl: String = "http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com",
    private val json: Json = Json { ignoreUnknownKeys = true },
) {
    private val cartService =
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(CartService::class.java)

    suspend fun getCartItems(
        offset: Int,
        limit: Int,
    ): List<CartContent> =
        withContext(Dispatchers.IO) {
            try {
                val response =
                    cartService.requestItems(
                        basicToken = "Basic ${authRepository.getAuthToken}",
                        page = offset,
                        size = limit,
                    )
                Log.d("cartItem", "${response.cartContent}")
                response.cartContent
            } catch (err: Exception) {
                Log.e("cartItem", "Unknown Error : $err")
                emptyList()
            }
        }

    suspend fun addItem(
        id: Long,
        quantity: Int,
    ) {
        withContext(Dispatchers.IO) {
            try {
                cartService.requestAddItem(
                    basicToken = "Basic ${authRepository.getAuthToken}",
                    addItemRequestBody = AddItemRequestBody(id, quantity),
                )
            } catch (err: HttpException) {
                when (err.code()) {
                    400 -> Log.e("cartItem", "Bad Request")
                    401 -> Log.e("cartItem", "Unauthorized")
                    403 -> Log.e("cartItem", "Forbidden")
                    404 -> Log.e("cartItem", "Not Found")
                    500 -> Log.e("cartItem", "Internal Server Error")
                }
            } catch (err: IOException) {
                Log.e("cartItem", "Network Error : $err")
            } catch (err: Exception) {
                Log.e("cartItem", "Unknown Error : $err")
            }
        }
    }
}
