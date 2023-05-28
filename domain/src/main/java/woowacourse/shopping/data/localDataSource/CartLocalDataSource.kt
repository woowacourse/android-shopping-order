package woowacourse.shopping.data.localDataSource

import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.CartProducts

interface CartLocalDataSource {
    fun getPage(index: Int, size: Int, callback: (Result<CartProducts>) -> Unit)
    fun hasNextPage(index: Int, size: Int): Boolean
    fun hasPrevPage(index: Int, size: Int): Boolean
    fun getTotalCount(): Int
    fun getTotalCheckedCount(): Int
    fun getTotalPrice(): Int
    fun insert(cartProduct: CartProduct)
    fun remove(id: Int, callback: () -> Unit)
    fun updateCount(id: Int, count: Int, callback: (Result<Int>) -> Unit)
    fun getAll(callback: (Result<CartProducts>) -> Unit)
}
