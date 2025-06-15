package woowacourse.shopping.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "ViewedItemEntity")
data class ViewedItem(
    @PrimaryKey val id: Long,
    val imageUrl: String,
    val name: String,
    val price: Int,
    val category: String,
    val viewedAt: Long = System.currentTimeMillis(),
)
