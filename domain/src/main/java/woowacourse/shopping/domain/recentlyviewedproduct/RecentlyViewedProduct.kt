package woowacourse.shopping.domain.recentlyviewedproduct

import woowacourse.shopping.domain.product.Product
import java.time.LocalDateTime

class RecentlyViewedProduct(
    val id: Long,
    val product: Product,
    val viewedTime: LocalDateTime
) {
    override fun equals(other: Any?): Boolean =
        if (other is RecentlyViewedProduct) id == other.id else false

    override fun hashCode(): Int = id.hashCode()
}
