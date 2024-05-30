package woowacourse.shopping.data.source

import woowacourse.shopping.data.model.ProductIdsCountData
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.remote.CartItemDto

interface ShoppingCartProductIdDataSource {
    fun findByProductId(productId: Long): ProductIdsCountData?

    fun loadPaged(page: Int): List<ProductIdsCountData>

    fun loadPagedItems(page: Int): List<CartItemDto>

    fun loadAll(): List<ProductIdsCountData>

    fun isFinalPage(page: Int): Boolean

    fun addedNewProductsId(productIdsCount: ProductIdsCount): Long

    fun removedProductsId(productId: Long): Long

    fun plusProductsIdCount(
        productId: Long,
        quantity: Int,
    )

    fun minusProductsIdCount(
        productId: Long,
        quantity: Int,
    )

    fun clearAll()
}
