package woowacourse.shopping.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class HistoryProductEntity(
    @PrimaryKey val productId: Long,
    val name: String,
    val imageUrl: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis(),
)
