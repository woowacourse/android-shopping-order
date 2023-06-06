package woowacourse.shopping.data.repository

import woowacourse.shopping.model.CartProduct

interface CartRepository {
    fun getAll(): Result<List<CartProduct>>
    fun getPage(offset: Int, size: Int): Result<List<CartProduct>>
    fun getCurrentPage(): Int
    fun getCurrentPageChecked(): Int
    fun getChecked(): Result<List<CartProduct>>
    fun getTotalQuantity(): Int
    fun getTotalCheckedQuantity(): Int
    fun getTotalCheckedPrice(): Int
    fun hasNextPage(): Boolean
    fun hasPrevPage(): Boolean
    fun updateCountWithProductId(productId: Int, count: Int): Result<Int>
    fun updateCurrentPageChecked(checked: Boolean)
    fun updateChecked(id: Int, checked: Boolean)
}
