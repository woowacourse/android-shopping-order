package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.ProductCount

typealias ProductId = Int
typealias CartProductId = Int

interface CartRepository {
    fun getAllCartProducts(): List<CartProduct>
    fun addCartProductByProductId(productId: ProductId)
    fun updateProductCountById(cartProductId: CartProductId, count: ProductCount)
    fun deleteCartProductById(cartProductId: CartProductId)
    fun findCartProductByProductId(productId: ProductId): CartProduct?
    fun increaseProductCountByProductId(productId: ProductId, addCount: ProductCount)
}
