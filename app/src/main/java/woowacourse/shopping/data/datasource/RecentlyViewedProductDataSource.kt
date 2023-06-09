package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.entity.RecentlyViewedProductEntity
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.recentlyviewedproduct.RecentlyViewedProduct
import java.time.LocalDateTime

interface RecentlyViewedProductDataSource {

    fun save(product: Product, viewedTime: LocalDateTime): Result<RecentlyViewedProduct>

    fun findLimitedOrderByViewedTimeDesc(limit: Int): Result<List<RecentlyViewedProductEntity>>
}
