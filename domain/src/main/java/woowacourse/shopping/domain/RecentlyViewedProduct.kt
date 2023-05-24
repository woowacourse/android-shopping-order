package woowacourse.shopping.domain

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
