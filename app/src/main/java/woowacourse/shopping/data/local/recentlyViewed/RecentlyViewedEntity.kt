package woowacourse.shopping.data.local.recentlyViewed

import java.time.LocalDateTime

data class RecentlyViewedEntity(
    val id: Long,
    val viewedDateTime: LocalDateTime,
    val productId: Long,
    val imageUrl: String,
    val name: String,
    val price: Int,
)
