package woowacourse.shopping.data.datasource.remote.cart

import com.example.domain.model.CartProduct

interface CartRemoteDataSource {

    fun loadAll(): List<CartProduct>

    fun addCartProduct(productId: Int): Int

    fun updateCartProductCount(cartProductId: Int, count: Int)

    fun deleteCartProduct(cartProductId: Int)
}
