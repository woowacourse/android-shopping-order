package woowacourse.shopping.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recentlyViewedProduct")
data class RecentlyViewedProduct(
    @PrimaryKey val productId: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String,
    val viewedAt: Long = System.currentTimeMillis(),
)
