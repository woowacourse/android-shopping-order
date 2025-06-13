package woowacourse.shopping.data.repository

import woowacourse.shopping.data.entity.CartProductEntity
import woowacourse.shopping.product.catalog.ProductUiModel

interface RecentlyViewedProductRepository {
    suspend fun insertRecentlyViewedProductUid(uid: Int)

    suspend fun getRecentlyViewedProducts(): List<CartProductEntity>

    suspend fun getLatestViewedProduct(): ProductUiModel?
}
