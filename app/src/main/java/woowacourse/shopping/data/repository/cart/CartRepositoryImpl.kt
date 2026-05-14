package woowacourse.shopping.data.repository.cart

import woowacourse.shopping.data.datasource.cart.CartRemoteDataSource
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.cart.CartItems
import woowacourse.shopping.domain.cart.Quantity
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val cartRemoteDataSource: CartRemoteDataSource,
) : CartRepository {
    override suspend fun getCartItems(
        page: Int,
        size: Int,
    ): CartItems = cartRemoteDataSource.getCartItems(page, size).toDomain()

    override suspend fun getCartItemsCount(): Int = cartRemoteDataSource.getCartItemsCount()

    override suspend fun getAllCartItems(): CartItems = cartRemoteDataSource.getCartItems(0, Int.MAX_VALUE).toDomain()

    override suspend fun addProduct(
        product: Product,
        quantity: Quantity,
    ) {
        cartRemoteDataSource.addCartItem(product.id, quantity.value)
    }

    override suspend fun increase(
        cartId: Int,
        quantity: Int,
    ) {
        cartRemoteDataSource.updateCartItem(cartId, quantity)
    }

    override suspend fun decrease(
        cartId: Int,
        quantity: Int,
    ) {
        cartRemoteDataSource.updateCartItem(cartId, quantity)
    }

    override suspend fun remove(cartId: Int) {
        cartRemoteDataSource.deleteCartItem(cartId)
    }

    override suspend fun order(cartItemIds: List<Int>) {
        cartRemoteDataSource.order(cartItemIds)
    }
}
