package woowacourse.shopping.data.datasource.recentproduct

import woowacourse.shopping.data.model.RecentProductEntity

interface RecentProductLocalDataSource {
    interface Local {
        fun getPartially(size: Int): List<RecentProductEntity>
        fun add(product: RecentProductEntity)
    }
}
