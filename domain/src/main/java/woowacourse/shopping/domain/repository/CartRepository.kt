package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.ProductCount

typealias ProductId = Int
typealias CartProductId = Int

interface CartRepository {
    fun getAllCartProducts(
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: () -> Unit,
    )

    fun addCartProductByProductId(
        productId: ProductId,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    )
    fun updateProductCountById(
        cartProductId: CartProductId,
        count: ProductCount,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    )
    fun deleteCartProductById(
        cartProductId: CartProductId,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    )
    fun findCartProductByProductId(
        productId: ProductId,
        onSuccess: (CartProduct) -> Unit,
        onFailure: () -> Unit,
    )

    fun increaseProductCountByProductId(productId: ProductId, addCount: ProductCount)

//    fun getAllCartProducts(): List<CartProduct>
//    fun addCartProductByProductId(productId: ProductId)
//    fun updateProductCountById(cartProductId: CartProductId, count: ProductCount)
//    fun deleteCartProductById(cartProductId: CartProductId)
//    fun findCartProductByProductId(productId: ProductId): CartProduct?
//    fun increaseProductCountByProductId(productId: ProductId, addCount: ProductCount)
}
