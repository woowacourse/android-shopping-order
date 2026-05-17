package woowacourse.shopping.data.localdb.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val id: String,
    val productId: String,
    val name: String,
    val price: Long,
    val imageUrl: String,
    val category: String,
    val quantity: Int,
    val timestamp: Long,
)
