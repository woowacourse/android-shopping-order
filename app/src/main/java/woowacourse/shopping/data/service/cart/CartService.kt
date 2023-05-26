package woowacourse.shopping.data.service.cart

import woowacourse.shopping.data.model.CartProduct
import woowacourse.shopping.data.model.ProductCount

typealias ProductId = Int
typealias CartProductId = Int

interface CartService {
    fun getAllCartProduct(): List<CartProduct>
    fun addCartProductByProductId(productId: ProductId)
    fun updateProductCountById(cartProductId: CartProductId, count: ProductCount)
    fun deleteCartProductById(cartProductId: CartProductId)
    fun findCartProductByProductId(productId: Int): CartProduct?
}
