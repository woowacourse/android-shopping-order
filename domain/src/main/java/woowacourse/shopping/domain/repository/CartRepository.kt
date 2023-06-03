package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartProduct

interface CartRepository {
    fun getAll(callback: (List<CartProduct>) -> Unit)
    fun insert(productId: Int, quantity: Int, callback: (Int) -> Unit)
    fun update(cartId: Int, quantity: Int, callback: (Boolean) -> Unit)
    fun remove(cartId: Int, callback: (Boolean) -> Unit)
}
