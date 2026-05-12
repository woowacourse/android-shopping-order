package woowacourse.shopping._archive.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_products")
data class RecentProductEntity(
    @PrimaryKey val productId: Long,
    val viewedAt: Long,
)
