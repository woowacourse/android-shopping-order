package woowacourse.shopping.data.cart.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import woowacourse.shopping.domain.model.Quantity

@Entity(tableName = "cart")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "product_id") val productId: Int,
    @ColumnInfo(name = "quantity") var quantity: Quantity,
)
