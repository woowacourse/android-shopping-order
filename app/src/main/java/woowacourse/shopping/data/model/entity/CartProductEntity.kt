package woowacourse.shopping.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_products")
data class CartProductEntity(
    @PrimaryKey val productId: Long,
    val quantity: Int,
)
