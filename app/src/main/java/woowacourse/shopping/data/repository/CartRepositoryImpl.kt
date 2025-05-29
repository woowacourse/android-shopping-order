package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.db.entity.CartEntity
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.CartSinglePage
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val dataSource: CartDataSource,
) : CartRepository {
    override fun getCart(
        id: Long,
        onResult: (cart: Cart?) -> Unit,
    ) {
        val entity = dataSource.getCartByProductId(id)
        onResult(entity?.toDomain())
    }

    override fun getCarts(
        productIds: List<Long>,
        onResult: (carts: List<Cart?>) -> Unit,
    ) {
        val entities = dataSource.getCartsByProductIds(productIds)
        onResult(entities.map { it?.toDomain() })
    }

    override fun upsert(
        productId: Long,
        quantity: Quantity,
    ) {
        val entity = CartEntity(productId = productId, quantity = quantity.value)
        dataSource.upsert(entity)
    }

    override fun delete(
        id: Long,
        onResult: (() -> Unit)?,
    ) {
        dataSource.deleteCartByProductId(id)
        onResult?.invoke()
    }

    override fun singlePage(
        page: Int,
        pageSize: Int,
        onResult: (CartSinglePage) -> Unit,
    ) {
        val items = dataSource.singlePage(page, pageSize).map { it.toDomain() }
        val pageCount = dataSource.pageCount(pageSize)
        val hasNextPage = page + 1 < pageCount
        onResult(CartSinglePage(items, hasNextPage))
    }
}
