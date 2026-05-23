package woowacourse.shopping.data.localdb.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_items")
data class RecentItemEntity(
    @PrimaryKey val productId: Long,
    val name: String,
    val imageUrl: String,
    val timestamp: Long,
)
