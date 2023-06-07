package woowacourse.shopping.data.datasource.recentproduct

import woowacourse.shopping.data.datasource.response.ProductEntity
import woowacourse.shopping.data.datasource.response.RecentProductEntity

interface RecentProductLocalDataSource {
    interface Local {
        fun getPartially(size: Int): List<RecentProductEntity>
        fun add(product: ProductEntity)
    }
}
