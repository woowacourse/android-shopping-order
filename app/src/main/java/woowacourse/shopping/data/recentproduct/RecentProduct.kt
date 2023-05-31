package woowacourse.shopping.data.recentproduct

import androidx.room.Entity

@Entity(primaryKeys = ["productId", "userInfo", "serverUrl"], tableName = "recent_product")
data class RecentProduct(
    val productId: Long,
    val userInfo: String,
    val serverUrl: String,
    val imageUrl: String,
    val name: String,
    val price: Int,
    val timestamp: Long,
)
