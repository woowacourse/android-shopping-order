package woowacourse.shopping.data.localDataSource

import woowacourse.shopping.model.CartProduct

interface CartLocalDataSource {
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
    fun updateCurrentPageChecked(checked: Boolean)
    fun updateChecked(id: Int, checked: Boolean)
    fun replaceAll(cartProducts: List<CartProduct>)
    fun getByProductId(productId: Int): Result<CartProduct>
}
