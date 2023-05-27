package woowacourse.shopping.data.localDataSource

import woowacourse.shopping.model.CartProducts

interface CartLocalDataSource {
    fun getPage(index: Int, size: Int, callback: (CartProducts) -> Unit)
    fun hasNextPage(index: Int, size: Int): Boolean
    fun hasPrevPage(index: Int, size: Int): Boolean
    fun getTotalCount(): Int
    fun getTotalSelectedCount(): Int
    fun getTotalPrice(): Int
    fun insert(productId: Int)
    fun remove(id: Int, callback: () -> Unit)
    fun updateCount(id: Int, count: Int, callback: (Int?) -> Unit)
    fun getAll(callback: (CartProducts) -> Unit)
}
