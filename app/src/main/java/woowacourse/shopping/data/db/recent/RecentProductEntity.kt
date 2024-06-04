package woowacourse.shopping.data.db.recent

import androidx.room.Entity
import androidx.room.PrimaryKey
import woowacourse.shopping.domain.model.RecentProduct
import java.time.LocalDateTime

@Entity(tableName = "recent_products")
data class RecentProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val productName: String,
    val imageUrl: String,
    val dateTime: String,
    val category: String,
)

fun RecentProductEntity.toRecentProduct(): RecentProduct {
    return RecentProduct(
        productId = this.productId,
        productName = this.productName,
        imageUrl = this.imageUrl,
        dateTime = LocalDateTime.parse(dateTime),
        category = this.category,
    )
}
