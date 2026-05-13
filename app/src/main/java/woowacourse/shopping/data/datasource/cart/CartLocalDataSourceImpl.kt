package woowacourse.shopping.data.datasource.cart

import woowacourse.shopping.data.local.cart.CartItemDao
import woowacourse.shopping.data.local.cart.CartItemEntity

class CartLocalDataSourceImpl(
    private val cartItemDao: CartItemDao,
) : CartLocalDataSource {
    override fun getCartItems() = cartItemDao.getCartItems()

    override suspend fun getCartItem(productId: Int) = cartItemDao.getCartItem(productId)

    override suspend fun increaseQuantity(
        productId: Int,
        amount: Int,
    ) = cartItemDao.increaseQuantity(productId, amount)

    override suspend fun decreaseQuantity(productId: Int) = cartItemDao.decreaseQuantity(productId)

    override suspend fun upsert(cartItem: CartItemEntity) = cartItemDao.upsert(cartItem)

    override suspend fun delete(productId: Int) = cartItemDao.delete(productId)
}
