package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.ProductCount

typealias ProductId = Int
typealias CartProductId = Int

interface CartRepository {
    fun getAllCartProducts(
        onSuccess: (List<CartProduct>) -> Unit, onFailed: (Throwable) -> Unit,
    )

    fun findCartProductByProductId(
        productId: ProductId,
        onSuccess: (CartProduct) -> Unit, onFailed: (Throwable) -> Unit,
    )

    fun saveCartProductByProductId(
        productId: ProductId,
        onSuccess: () -> Unit, onFailed: (Throwable) -> Unit,
    )

    fun updateProductCountById(
        cartProductId: CartProductId, count: ProductCount,
        onSuccess: () -> Unit, onFailed: (Throwable) -> Unit,
    )

    fun deleteCartProductById(
        cartProductId: CartProductId,
        onSuccess: () -> Unit, onFailed: (Throwable) -> Unit,
    )

    fun increaseProductCountByProductId(
        productId: ProductId, addCount: ProductCount,
        onSuccess: () -> Unit, onFailed: (Throwable) -> Unit,
    )
}
