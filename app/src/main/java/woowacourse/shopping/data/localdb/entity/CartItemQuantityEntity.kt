package woowacourse.shopping.data.localdb.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_item_quantities")
data class CartItemQuantityEntity(
    @PrimaryKey val productId: Long,
    val cartItemId: Long,
    val quantity: Int,
)
