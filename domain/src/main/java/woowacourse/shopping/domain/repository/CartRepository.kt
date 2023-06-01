package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.ProductCount

typealias ProductId = Int
typealias CartProductId = Int

interface CartRepository {
    fun getAllCartProducts(
        onSuccess: (List<CartProduct>) -> Unit,
        onFailed: (Throwable) -> Unit,
    )

    fun findCartProductByProductId(
        productId: ProductId,
        onSuccess: (CartProduct) -> Unit,
        onFailed: (Throwable) -> Unit,
    )

    fun addCartProductByProductId(productId: ProductId)
    fun updateProductCountById(cartProductId: CartProductId, count: ProductCount)
    fun deleteCartProductById(cartProductId: CartProductId)
    fun increaseProductCountByProductId(productId: ProductId, addCount: ProductCount)
}
