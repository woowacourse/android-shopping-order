package woowacourse.shopping.data.model

import com.google.gson.annotations.SerializedName
import woowacourse.shopping.data.local.database.RecentProductEntity
import java.time.LocalDateTime

data class Product(
    val category: String,
    val id: Int,
    val imageUrl: String,
    val name: String,
    val price: Int,
)

fun Product.toRecentProductEntity(): RecentProductEntity {
    val localDateTime = LocalDateTime.now().toString()
    return RecentProductEntity(
        productId = this.id,
        productName = this.name,
        imageUrl = this.imageUrl,
        dateTime = localDateTime,
        category = this.category,
    )
}
