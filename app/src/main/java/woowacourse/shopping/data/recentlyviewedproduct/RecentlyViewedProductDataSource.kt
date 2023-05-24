package woowacourse.shopping.data.recentlyviewedproduct

import woowacourse.shopping.data.entity.RecentlyViewedProductEntity
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.RecentlyViewedProduct
import java.time.LocalDateTime

interface RecentlyViewedProductDataSource {

    fun save(product: Product, viewedTime: LocalDateTime, onFinish: (RecentlyViewedProduct) -> Unit)

    fun findFirst10OrderByViewedTimeDesc(onFinish: (List<RecentlyViewedProductEntity>) -> Unit)
}
