package woowacourse.shopping.data.model

import com.google.gson.annotations.SerializedName
import woowacourse.shopping.data.db.recent.RecentProductEntity
import java.time.LocalDateTime

data class Product(
    @SerializedName("category")
    val category: String,
    @SerializedName("id")
    val productId: Int,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Int,
)

fun Product.toRecentProductEntity(): RecentProductEntity {
    val localDateTime = LocalDateTime.now().toString()
    return RecentProductEntity(
        productId = this.productId,
        productName = this.name,
        imageUrl = this.imageUrl,
        dateTime = localDateTime,
        category = this.category,
    )
}
