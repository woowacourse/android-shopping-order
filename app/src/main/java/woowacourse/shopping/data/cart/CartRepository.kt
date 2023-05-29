package woowacourse.shopping.data.cart

import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.Product
import woowacourse.shopping.presentation.model.CartProductModel

interface CartRepository {
    fun insertCartProduct(productId: Long, count: Int, callback: () -> Unit)
    fun updateCartProductCount(cartId: Long, count: Int, callback: () -> Unit)
    fun deleteCartProduct(cartId: Long, callback: () -> Unit)
    fun getCartProducts(callback: (List<CartProduct>) -> Unit)
    fun getProductsByRange(
        lastProductId: Long,
        pageItemCount: Int,
        callback: (List<CartProductModel>, Boolean) -> Unit,
    )

    fun findProductById(productId: Long, callback: (Product) -> Unit)
}
