package woowacourse.shopping.data.datasource.recentproduct

import woowacourse.shopping.data.model.DataProduct
import woowacourse.shopping.data.model.DataRecentProduct

interface RecentProductLocalDataSource {
    interface Local {
        fun getPartially(size: Int): List<DataRecentProduct>
        fun add(product: DataProduct)
    }
}
