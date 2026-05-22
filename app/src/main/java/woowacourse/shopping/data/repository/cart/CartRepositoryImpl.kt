package woowacourse.shopping.data.repository.cart

import woowacourse.shopping.data.datasource.cart.CartDataSource
import woowacourse.shopping.data.mapper.toCartItems
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.mapper.toPagedCartItems
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
    ): PagedCartItems = cartRemoteDataSource.getCartItems(page, size).toPagedCartItems()

    override suspend fun getCartItemsCount(): Int = cartRemoteDataSource.getCartItemsCount()

    override suspend fun getAllCartItems(): CartItems = cartRemoteDataSource.getCartItems(0, Int.MAX_VALUE).toCartItems()

    override suspend fun addProduct(
        product: Product,
        quantity: Quantity,
    ): Int {
        cartRemoteDataSource.addCartItem(product.id, quantity)
        return getAllCartItems().findByProductId(product.id)?.id
            ?: throw IllegalStateException("장바구니에 상품이 추가되지 않았습니다.")
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
