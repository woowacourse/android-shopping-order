package woowacourse.shopping.data.datasource.remote.cart

import com.example.domain.model.cart.CartProduct
import woowacourse.shopping.data.datasource.remote.RetrofitService
import woowacourse.shopping.data.model.cart.CartProductDto
import woowacourse.shopping.data.model.toDomain

class CartDataSourceImpl : CartRemoteDataSource {

    private val cartService = RetrofitService.cartService

    override fun loadAll(): List<CartProduct> {

        val cartProducts = mutableListOf<CartProductDto>()

        val thread = Thread {
            val response = cartService.requestCartProducts().execute()
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
            val response = cartService.addCartProduct(productId).execute()
            val location = response.headers()["Location"]
            location?.filter { it.isDigit() }?.let { cartItemId = it.toInt() }
        }
        thread.start()
        thread.join()

        return cartItemId
    }

    override fun updateCartProductCount(cartProductId: Int, count: Int) {

        val thread = Thread {
            cartService.updateCartProduct(cartProductId, count).execute()
        }
        thread.start()
        thread.join()
    }

    override fun deleteCartProduct(cartProductId: Int) {

        val thread = Thread {
            cartService.deleteCartProduct(cartProductId).execute()
        }
        thread.start()
        thread.join()
    }
}
