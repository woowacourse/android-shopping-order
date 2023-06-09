package woowacourse.shopping.data.local

import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.CartProductPage

interface CartLocalDataSource {
    fun getAll(): Result<List<CartProduct>>
    fun getPage(offset: Int, size: Int): Result<CartProductPage>
    fun getChecked(): Result<List<CartProduct>>
    fun getTotalQuantity(): Int
    fun getTotalCheckedQuantity(): Int
    fun getTotalCheckedPrice(): Int
    fun getCheckCount(ids: List<Int>): Int
    fun replaceAll(cartProducts: List<CartProduct>)
    fun getByProductId(productId: Int): Result<CartProduct>
    fun updateChecked(id: Int, checked: Boolean)
    fun updateChecked(ids: List<Int>, checked: Boolean)
}
