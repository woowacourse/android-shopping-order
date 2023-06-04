package woowacourse.shopping.data.localDataSource

import woowacourse.shopping.model.CartProduct

interface CartLocalDataSource {
    fun getPage(offset: Int, size: Int): Result<List<CartProduct>>
    fun getChecked(): Result<List<CartProduct>>
    fun getByProductId(productId: Int): Result<CartProduct>
    fun getCurrentPage(): Int
    fun getTotalCount(): Int
    fun getTotalPrice(): Int
    fun getTotalCheckedCount(): Int
    fun getCurrentPageChecked(): Int
    fun setCurrentPageChecked(checked: Boolean)
    fun hasNextPage(): Boolean
    fun hasPrevPage(): Boolean
    fun replaceAll(cartProducts: List<CartProduct>)
    fun updateChecked(id: Int, checked: Boolean)
}
