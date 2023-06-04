package woowacourse.shopping.data.localDataSource

import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.CartProducts

interface CartLocalDataSource {
    fun getPage(index: Int, size: Int): Result<CartProducts>
    fun hasNextPage(index: Int, size: Int): Boolean
    fun hasPrevPage(index: Int, size: Int): Boolean
    fun getTotalCount(): Int
    fun getTotalCheckedCount(): Int
    fun getTotalPrice(): Int
    fun insert(cartProduct: CartProduct): Result<Int>
    fun remove(id: Int): Result<Int>
    fun updateCount(id: Int, count: Int): Result<Int>
    fun getAll(): Result<CartProducts>
}
