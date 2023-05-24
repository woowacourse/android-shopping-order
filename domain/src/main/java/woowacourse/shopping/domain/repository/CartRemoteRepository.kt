package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.ProductCount

typealias ProductId = Int
typealias CartProductId = Int

interface CartRemoteRepository {
    fun getAllCartProduct(): List<CartProduct>
    fun addCartProductByProductId(productId: ProductId)
    fun updateProductCountById(cartProductId: CartProductId, count: ProductCount)
    fun deleteCartProductById(cartProductId: CartProductId)
}
