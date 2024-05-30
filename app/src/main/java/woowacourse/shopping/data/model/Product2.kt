package woowacourse.shopping.data.model

import com.google.gson.annotations.SerializedName
import woowacourse.shopping.data.db.cart.CartItemEntity
import woowacourse.shopping.data.db.recent.RecentProductEntity
import woowacourse.shopping.domain.model.Product
import java.time.LocalDateTime

data class Product2(
    @SerializedName("category")
    val category: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Int,
)

fun Product2.toCartItemEntity(quantity: Int): CartItemEntity {
    return CartItemEntity(
        productId = this.id,
        productName = this.name,
        price = this.price,
        imageUrl = this.imageUrl,
        quantity = quantity,
    )
}

fun Product2.toRecentProductEntity(): RecentProductEntity {
    val localDateTime = LocalDateTime.now().toString()
    return RecentProductEntity(
        productId = this.id,
        productName = this.name,
        imageUrl = this.imageUrl,
        dateTime = localDateTime,
        category = this.category
    )
}

