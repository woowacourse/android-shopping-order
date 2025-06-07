package woowacourse.shopping.data.datasource.local

import woowacourse.shopping.data.entity.RecentlyViewedProduct

interface RecentProductLocalDataSource {
    suspend fun getProducts(): List<RecentlyViewedProduct>

    suspend fun getMostRecentProduct(): RecentlyViewedProduct?

    suspend fun getOldestProduct(): RecentlyViewedProduct

    suspend fun getCount(): Int

    suspend fun insert(product: RecentlyViewedProduct)

    suspend fun delete(product: RecentlyViewedProduct)
}
