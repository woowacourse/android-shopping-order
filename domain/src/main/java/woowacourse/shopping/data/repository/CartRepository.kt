package woowacourse.shopping.data.repository

import woowacourse.shopping.model.CartProducts

interface CartRepository {
    fun getAll(): Result<CartProducts>
    fun getPage(index: Int, size: Int): Result<CartProducts>
    fun getTotalCount(): Int
    fun getTotalSelectedCount(): Int
    fun getTotalPrice(): Int
    fun hasNextPage(index: Int, size: Int): Boolean
    fun hasPrevPage(index: Int, size: Int): Boolean
    fun updateCountWithProductId(productId: Int, count: Int): Result<Int>
    fun updateChecked(id: Int, checked: Boolean)
    fun remove(id: Int): Result<Int>
    fun insert(productId: Int): Result<Int>
}
