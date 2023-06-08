package woowacourse.shopping.data.datasource.remote.cart

import com.example.domain.model.CartProduct
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import woowacourse.shopping.data.datasource.remote.ServerInfo
import woowacourse.shopping.data.model.cart.CartProductDto
import woowacourse.shopping.data.model.toDomain

class CartDataSourceImpl(private val credential: String) : CartRemoteDataSource {

    private val retrofitService = Retrofit.Builder()
        .baseUrl(ServerInfo.currentBaseUrl)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(CartService::class.java)

    override fun loadAll(): List<CartProduct> {

        val cartProducts = mutableListOf<CartProductDto>()

        val thread = Thread {
            val response = retrofitService.requestCartProducts("Basic $credential").execute()
            val value = response.body() ?: emptyList()
            cartProducts.addAll(value)
        }
        thread.start()
        thread.join()

        return cartProducts.map { it.toDomain() }
    }

    override fun addCartProduct(productId: Int): Int {

        var cartItemId = -1

        val thread = Thread {
            val response = retrofitService.addCartProduct("Basic $credential", productId).execute()
            val location = response.headers()["Location"]
            location?.filter { it.isDigit() }?.let { cartItemId = it.toInt() }
        }
        thread.start()
        thread.join()

        return cartItemId
    }

    override fun updateCartProductCount(cartProductId: Int, count: Int) {

        val thread = Thread {
            retrofitService.updateCartProduct("Basic $credential", cartProductId, count).execute()
        }
        thread.start()
        thread.join()
    }

    override fun deleteCartProduct(cartProductId: Int) {

        val thread = Thread {
            retrofitService.deleteCartProduct("Basic $credential", cartProductId).execute()
        }
        thread.start()
        thread.join()
    }
}
