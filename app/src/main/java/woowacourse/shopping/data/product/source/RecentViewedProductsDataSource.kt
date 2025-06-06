package woowacourse.shopping.data.product.source

import woowacourse.shopping.data.product.entity.RecentViewedProductEntity

interface RecentViewedProductsDataSource {
    suspend fun load(): List<RecentViewedProductEntity>

    suspend fun upsert(product: RecentViewedProductEntity)
}
