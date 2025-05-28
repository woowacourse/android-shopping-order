package woowacourse.shopping.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecentProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val productId: Long,
    val name: String,
    val imageUrl: String,
    val price: Int,
    val viewedAt: Long = System.currentTimeMillis(),
)
