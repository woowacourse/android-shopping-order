package woowacourse.shopping.data.source.cart

import woowacourse.shopping.data.model.ProductIdsCountData
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.remote.cart.CartItemDto

interface CartItemDataSource {
    fun findByProductId(productId: Long): ProductIdsCountData?

    fun loadPaged(page: Int): List<ProductIdsCountData>

    fun loadPagedItems(page: Int): List<CartItemDto>

    fun loadAll(): List<ProductIdsCountData>

    fun loadAllCartItems(): List<CartItemDto>

    fun isFinalPage(page: Int): Boolean

    fun addedNewProductsId(productIdsCount: ProductIdsCount): Long

    fun removedProductsId(productId: Long): Long

    fun plusProductsIdCount(
        cartItemId: Long,
        quantity: Int,
    )

    fun minusProductsIdCount(
        cartItemId: Long,
        quantity: Int,
    )

    fun clearAll()
}
