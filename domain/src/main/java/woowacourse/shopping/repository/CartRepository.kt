package woowacourse.shopping.repository

import woowacourse.shopping.model.CartProducts

interface CartRepository {
    fun getPage(
        index: Int,
        size: Int,
        onSuccess: (CartProducts) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    fun getAll(
        onSuccess: (CartProducts) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    fun remove(
        id: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    )

    fun updateCount(
        id: Int,
        count: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    )

    fun hasNextPage(index: Int, size: Int): Boolean
    fun hasPrevPage(index: Int, size: Int): Boolean
    fun getTotalCount(): Int
    fun getTotalSelectedCount(): Int
    fun getTotalPrice(): Int
    fun updateChecked(id: Int, checked: Boolean)
    fun insert(productId: Int)
    fun getCheckedIds(): List<Int>
}
