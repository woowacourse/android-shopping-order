package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.source.ShoppingCartProductIdDataSource
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.domain.model.toDomain
import woowacourse.shopping.remote.CartItemDto

class DefaultProductIdsCountRepository(
    private val productsIdsCountDataSource: ShoppingCartProductIdDataSource,
) : ProductIdsCountRepository {
    override fun findByProductId(productId: Long): ProductIdsCount =
        productsIdsCountDataSource.findByProductId(productId)?.toDomain()
            ?: throw NoSuchElementException()

    override fun loadAllProductIdsCounts(): List<ProductIdsCount> =
        productsIdsCountDataSource.loadAll().map {
            it.toDomain()
        }

    override fun loadPagedCartItem(): List<CartItemDto> {
        return productsIdsCountDataSource.loadAllCartItems()
    }

    override fun addedProductsId(productIdsCount: ProductIdsCount): Long = productsIdsCountDataSource.addedNewProductsId(productIdsCount)

    override fun removedProductsId(productId: Long): Long = productsIdsCountDataSource.removedProductsId(productId)

    override fun plusProductsIdCount(
        productId: Long,
        quantity: Int,
    ) {
        productsIdsCountDataSource.plusProductsIdCount(productId, quantity)
    }

    override fun minusProductsIdCount(
        productId: Long,
        quantity: Int,
    ) {
        productsIdsCountDataSource.minusProductsIdCount(productId, quantity)
    }

    override fun clearAll() {
        productsIdsCountDataSource.clearAll()
    }
}
