package woowacourse.shopping.data.repository

import woowacourse.shopping.data.entity.CartProductEntity
import woowacourse.shopping.product.catalog.ProductUiModel

interface RecentlyViewedProductRepository {
    suspend fun insertRecentlyViewedProductId(productId: Long): Result<Unit>

    suspend fun getRecentlyViewedProducts(): Result<List<CartProductEntity>>

    suspend fun getLatestViewedProduct(): Result<ProductUiModel?>
}
