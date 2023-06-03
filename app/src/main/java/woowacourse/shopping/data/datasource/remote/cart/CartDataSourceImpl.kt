package woowacourse.shopping.data.datasource.remote.cart

import com.example.domain.model.CartProduct
import woowacourse.shopping.data.model.toDomain

class CartDataSourceImpl(
    private val cartService: CartService
) : CartRemoteDataSource {

    override fun loadAll(): List<CartProduct> {
        return cartService.getAll().execute().body()?.map { it.toDomain() } ?: emptyList()
    }

    override fun addCartProduct(productId: Int): Int {
        val location = cartService.addProduct(productId).execute().headers()["Location"]
        return location?.filter { it.isDigit() }?.toInt() ?: -1
    }

    override fun updateCartProductCount(cartProductId: Int, count: Int) {
        cartService.updateQuantity(cartProductId, count).execute()
    }

    override fun deleteCartProduct(cartProductId: Int) {
        cartService.deleteProduct(cartProductId).execute()
    }
}
