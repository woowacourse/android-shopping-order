package woowacourse.shopping.data.datasource.remote.cart

import com.example.domain.model.CartProduct
import woowacourse.shopping.data.model.toDomain

class CartRetrofitService(
    private val cartApi: CartApi
) : CartRemoteDataSource {

    override fun loadAll(): List<CartProduct> {
        return cartApi.getAll().execute().body()?.map { it.toDomain() } ?: emptyList()
    }

    override fun addCartProduct(productId: Int): Int {
        val location = cartApi.addProduct(productId).execute().headers()["Location"]
        return location?.filter { it.isDigit() }?.toInt() ?: -1
    }

    override fun updateCartProductCount(cartProductId: Int, count: Int) {
        cartApi.updateQuantity(cartProductId, count).execute()
    }

    override fun deleteCartProduct(cartProductId: Int) {
        cartApi.deleteProduct(cartProductId).execute()
    }
}
