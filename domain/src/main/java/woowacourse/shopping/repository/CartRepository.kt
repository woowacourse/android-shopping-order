package woowacourse.shopping.repository

import woowacourse.shopping.model.CartProducts
import woowacourse.shopping.model.OrderInfo

interface CartRepository {
    fun getPage(index: Int, size: Int, callback: (CartProducts) -> Unit)
    fun hasNextPage(index: Int, size: Int): Boolean
    fun hasPrevPage(index: Int, size: Int): Boolean
    fun getTotalCount(): Int
    fun getTotalSelectedCount(): Int
    fun getTotalPrice(): Int
    fun updateCount(id: Int, count: Int, callback: (Int?) -> Unit)
    fun updateChecked(id: Int, checked: Boolean)
    fun getAll(callback: (CartProducts) -> Unit)
    fun remove(id: Int, callback: () -> Unit)
    fun insert(productId: Int)
    fun getCheckedIds(): List<Int>
    fun getOrderInfo(ids: List<Int>, callback: (OrderInfo?) -> Unit)
}
