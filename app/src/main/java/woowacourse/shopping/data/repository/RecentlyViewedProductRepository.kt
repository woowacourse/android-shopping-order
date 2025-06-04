package woowacourse.shopping.data.repository

import woowacourse.shopping.data.entity.CartProductEntity
import woowacourse.shopping.product.catalog.ProductUiModel

interface RecentlyViewedProductRepository {
    fun insertRecentlyViewedProductId(uid: Int)

    fun getRecentlyViewedProducts(callback: (List<CartProductEntity>) -> Unit)

    fun getLatestViewedProduct(callback: (ProductUiModel) -> Unit)
}
