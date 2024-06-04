package woowacourse.shopping.data.repsoitory

import woowacourse.shopping.data.datasource.remote.ShoppingRemoteCartDataSource
import woowacourse.shopping.domain.model.Carts
import woowacourse.shopping.domain.repository.ShoppingCartRepository

class ShoppingCartRepositoryImpl(private val dataSource: ShoppingRemoteCartDataSource) :
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
    ): Result<Carts> = dataSource.getCartProductsPaged(page = page, size = size)

    override fun getCartProductsQuantity(): Result<Int> = dataSource.getCartProductsQuantity()

    override fun deleteCartProductById(cartId: Int): Result<Unit> = dataSource.deleteCartProductById(cartId = cartId)

    override fun getAllCarts(): Result<Carts> {
        val totalElements =
            dataSource.getCartProductsPaged(
                page = ProductRepositoryImpl.FIRST_PAGE,
                size = ProductRepositoryImpl.FIRST_SIZE,
            ).getOrThrow().totalElements

        return dataSource.getCartProductsPaged(
            page = ProductRepositoryImpl.FIRST_PAGE,
            size = totalElements,
        )
    }
}
