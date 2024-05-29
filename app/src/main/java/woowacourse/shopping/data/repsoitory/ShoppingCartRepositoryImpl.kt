package woowacourse.shopping.data.repsoitory

import woowacourse.shopping.data.datasource.remote.ShoppingCartDataSource
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.model.Carts
import woowacourse.shopping.domain.repository.ShoppingCartRepository

class ShoppingCartRepositoryImpl(private val dataSource: ShoppingCartDataSource) :
    ShoppingCartRepository {
    override fun insertCartProduct(
        productId: Long,
        quantity: Int,
    ): Result<Int> =
        dataSource.insertCartProduct(
            productId = productId,
            quantity = quantity,
        )

    override fun updateCartProduct(
        cartId: Int,
        quantity: Int,
    ): Result<Unit> = dataSource.updateCartProduct(cartId = cartId, quantity = quantity)

    override fun getCartProductsPaged(
        page: Int,
        size: Int,
    ): Result<Carts> =
        dataSource.getCartProductsPaged(page = page, size = size)
            .mapCatching { result -> result.toDomain() }

    override fun getCartProductsTotal(): Result<Int> = dataSource.getCartProductsTotal()

    override fun deleteCartProduct(cartId: Int): Result<Unit> = dataSource.deleteCartProduct(cartId = cartId)
}
