package woowacourse.shopping.repository

import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.RecentlyViewedProduct
import java.time.LocalDateTime

interface RecentlyViewedProductRepository {

    fun save(product: Product, viewedTime: LocalDateTime, onFinish: (RecentlyViewedProduct) -> Unit)

    fun findFirst10OrderByViewedTimeDesc(onFinish: (List<RecentlyViewedProduct>) -> Unit)
}
