package woowacourse.shopping.data.datasource.recentlyviewedproduct

import woowacourse.shopping.data.entity.RecentlyViewedProductEntity
import woowacourse.shopping.domain.RecentlyViewedProduct

interface RecentlyViewedProductDataSource {

    fun save(recentlyViewedProduct: RecentlyViewedProduct)

    fun findFirst10OrderByViewedTimeDesc(onFinish: (List<RecentlyViewedProductEntity>) -> Unit)
}
