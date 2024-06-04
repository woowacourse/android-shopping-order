package woowacourse.shopping.data.repsoitory

import woowacourse.shopping.data.datasource.remote.ShoppingCartDataSource
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.model.CartItemId
import woowacourse.shopping.domain.model.Carts
import woowacourse.shopping.domain.repository.ShoppingCartRepository

class ShoppingCartRepositoryImpl(private val dataSource: ShoppingCartDataSource) :
    ShoppingCartRepository {
    override fun postCartItem(
        productId: Long,
        quantity: Int,
    ): Result<CartItemId> =
        dataSource.postCartItem(
            productId = productId,
            quantity = quantity,
        ).mapCatching { it.toDomain() }

    override fun patchCartItem(
        cartId: Int,
        quantity: Int,
    ): Result<Unit> = dataSource.patchCartItem(cartId = cartId, quantity = quantity)

    override fun getCartProductsPaged(
        page: Int,
        size: Int,
    ): Result<Carts> =
        dataSource.getCartProductsPaged(page = page, size = size)
            .mapCatching { result -> result.toDomain() }

    override fun getCartItemsCount(): Result<Int> = dataSource.getCartItemsCount()

    override fun deleteCartItem(cartId: Int): Result<Unit> = dataSource.deleteCartItem(cartId = cartId)

    override fun getAllCarts(): Result<Carts> {
        val totalElements =
            dataSource.getCartProductsPaged(
                page = ProductRepositoryImpl.FIRST_PAGE,
                size = ProductRepositoryImpl.FIRST_SIZE,
            ).getOrThrow().totalElements

        return dataSource.getCartProductsPaged(
            page = ProductRepositoryImpl.FIRST_PAGE,
            size = totalElements,
        ).mapCatching { it.toDomain() }
    }
}
