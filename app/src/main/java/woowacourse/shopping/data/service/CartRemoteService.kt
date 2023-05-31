package woowacourse.shopping.data.service

import com.example.domain.model.CartProduct
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.model.CartProductDto
import woowacourse.shopping.data.model.toDomain

class CartRemoteService(private val credential: String) {

    private val retrofitService = Retrofit.Builder()
        .baseUrl(ServerInfo.currentBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RetrofitService::class.java)

    fun loadAll(): List<CartProduct> {

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

    fun addCartProduct(productId: Int): Int {

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

    fun updateCartProductCount(cartProductId: Int, count: Int) {

        val thread = Thread {
            retrofitService.updateCartProduct("Basic $credential", cartProductId, count).execute()
        }
        thread.start()
        thread.join()
    }

    fun deleteCartProduct(cartProductId: Int) {

        val thread = Thread {
            retrofitService.deleteCartProduct("Basic $credential", cartProductId).execute()
        }
        thread.start()
        thread.join()
    }
}
