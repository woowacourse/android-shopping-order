package woowacourse.shopping.repository

import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.recentlyviewedproduct.RecentlyViewedProduct
import java.time.LocalDateTime

interface RecentlyViewedProductRepository {

    fun save(
        product: Product,
        viewedTime: LocalDateTime
    ): Result<RecentlyViewedProduct>

    fun findFirst10OrderByViewedTimeDesc(): Result<List<RecentlyViewedProduct>>
}
