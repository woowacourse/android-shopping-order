package woowacourse.shopping.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ViewedItemEntity")
data class ViewedItem(
    @PrimaryKey val id: Int,
    val imageUrl: String,
    val name: String,
    val price: Int,
    val viewedAt: Long = System.currentTimeMillis(),
)
