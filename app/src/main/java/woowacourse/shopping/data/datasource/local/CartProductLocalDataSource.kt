package woowacourse.shopping.data.datasource.local

import woowacourse.shopping.data.dao.CartProductDao

class CartProductLocalDataSource(
    private val dao: CartProductDao,
) {
    fun getTotalCount(): Int = dao.getTotalCount()

    fun getQuantityByProductId(productId: Long): Int? = dao.getQuantityByProductId(productId)

    fun getTotalQuantity(): Int = dao.getTotalQuantity()

    fun updateQuantity(
        productId: Long,
        quantity: Int,
    ) = dao.updateQuantity(productId, quantity)

    fun deleteByProductId(productId: Long) = dao.deleteByProductId(productId)
}
