package woowacourse.shopping.data.product.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "recentViewedProducts")
class RecentViewedProductEntity(
    @PrimaryKey
    val productId: Long,
    val viewedAt: LocalDateTime,
)
