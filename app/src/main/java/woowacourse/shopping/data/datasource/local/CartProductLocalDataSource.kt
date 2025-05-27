package woowacourse.shopping.data.datasource.local

import woowacourse.shopping.data.dao.CartProductDao

class CartProductLocalDataSource(
    private val dao: CartProductDao,
) {
    fun getQuantityByProductId(productId: Int): Int? = dao.getQuantityByProductId(productId)

    fun updateQuantity(
        productId: Int,
        quantity: Int,
    ) = dao.updateQuantity(productId, quantity)
}
