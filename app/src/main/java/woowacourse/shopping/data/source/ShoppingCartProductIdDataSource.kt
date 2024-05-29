package woowacourse.shopping.data.source

import woowacourse.shopping.data.model.ProductIdsCountData
import woowacourse.shopping.domain.model.ProductIdsCount

interface ShoppingCartProductIdDataSource {
    fun findByProductId(productId: Long): ProductIdsCountData?

    fun loadPaged(page: Int): List<ProductIdsCountData>

    fun loadAll(): List<ProductIdsCountData>

    fun isFinalPage(page: Int): Boolean

    fun addedNewProductsId(productIdsCount: ProductIdsCount): Long

    fun removedProductsId(productId: Long): Long

    fun plusProductsIdCount(productId: Long)

    fun minusProductsIdCount(productId: Long)

    fun clearAll()
}
