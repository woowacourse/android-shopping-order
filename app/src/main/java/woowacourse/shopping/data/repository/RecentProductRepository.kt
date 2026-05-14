package woowacourse.shopping.data.repository

import woowacourse.shopping.data.model.Product
import woowacourse.shopping.data.model.Products
interface RecentProductRepository {
    suspend fun getRecentProducts(): Products

    suspend fun getLastViewedProduct(): Product?

    suspend fun add(productId: Long)
}
