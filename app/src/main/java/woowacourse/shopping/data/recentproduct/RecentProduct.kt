package woowacourse.shopping.data.recentproduct

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_product")
data class RecentProduct(
    @PrimaryKey val productId: Long,
    val imageUrl: String,
    val name: String,
    val price: Int,
    val timestamp: Long,
)
