package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartProduct

interface CartRepository {
    fun findAll(callback: (List<CartProduct>) -> Unit)
    fun insert(productId: Int, callback: (cartItemId: Int) -> Unit)
    fun update(cartId: Int, count: Int, callback: (Boolean) -> Unit)
    fun remove(cartId: Int, callback: (Boolean) -> Unit)
    fun findRange(mark: Int, rangeSize: Int, callback: (List<CartProduct>) -> Unit)
    fun isExistByMark(mark: Int, callback: (Boolean) -> Unit)
}
