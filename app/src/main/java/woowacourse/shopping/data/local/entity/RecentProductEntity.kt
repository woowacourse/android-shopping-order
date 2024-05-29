package woowacourse.shopping.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecentProductEntity(
    @PrimaryKey
    val productId: Long,
    val name: String,
    val imgUrl: String,
    val quantity: Int,
    val price: Long,
    val createdAt: Long,
    val category: String,
    val cartId: Long
)
