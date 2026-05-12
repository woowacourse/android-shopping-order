package woowacourse.shopping._archive.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey val productId: Long,
    val quantity: Int,
)
