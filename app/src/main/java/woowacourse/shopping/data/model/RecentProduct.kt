package woowacourse.shopping.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(indices = [Index(value = ["productId"], unique = true)], tableName = "recent_products")
data class RecentProduct(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val productId: Long,
    val recentTime: LocalDateTime,
)
