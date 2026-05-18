package woowacourse.shopping.data.repository.cart

import woowacourse.shopping.data.datasource.cart.CartDataSource
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.model.cart.CartItems
import woowacourse.shopping.domain.model.cart.Quantity
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.ui.cart.PagedCartItems

class CartRepositoryImpl(
    private val cartRemoteDataSource: CartDataSource,
) : CartRepository {
    override suspend fun getCartItems(
        page: Int,
        size: Int,
    ): PagedCartItems = cartRemoteDataSource.getCartItems(page, size).toDomain()

    override suspend fun getCartItemsCount(): Int = cartRemoteDataSource.getCartItemsCount()

    override suspend fun getAllCartItems(): PagedCartItems = cartRemoteDataSource.getCartItems(0, Int.MAX_VALUE).toDomain()

    override suspend fun addProduct(
        product: Product,
        quantity: Quantity,
    ) {
        cartRemoteDataSource.addCartItem(product.id, quantity)
    }

    override suspend fun increase(
        cartId: Int,
        quantity: Quantity,
    ) {
        cartRemoteDataSource.updateCartItem(cartId, quantity)
    }

    override suspend fun decrease(
        cartId: Int,
        quantity: Quantity,
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
