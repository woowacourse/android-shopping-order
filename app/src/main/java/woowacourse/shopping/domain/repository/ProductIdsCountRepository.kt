package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.remote.CartItemDto

interface ProductIdsCountRepository {
    fun findByProductId(productId: Long): ProductIdsCount

    fun loadAllProductIdsCounts(): List<ProductIdsCount>

    fun loadPagedCartItem(): List<CartItemDto>

    fun addedProductsId(productIdsCount: ProductIdsCount): Long

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
