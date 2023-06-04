package woowacourse.shopping.data.repository

import woowacourse.shopping.model.CartProduct

interface CartRepository {
    fun getAll(): Result<List<CartProduct>>
    fun getPage(offset: Int, size: Int): Result<List<CartProduct>>
    fun getTotalCount(): Int
    fun getTotalCheckedCount(): Int
    fun getTotalPrice(): Int
    fun hasNextPage(): Boolean
    fun hasPrevPage(): Boolean
    fun updateCountWithProductId(productId: Int, count: Int): Result<Int>
    fun updateChecked(id: Int, checked: Boolean)
    fun updateAllChecked(checked: Boolean)
    fun getCurrentPage(): Int
    fun getCurrentPageChecked(): Int
    fun getChecked(): Result<List<CartProduct>>
}
