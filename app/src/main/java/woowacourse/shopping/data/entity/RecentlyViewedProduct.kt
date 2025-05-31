package woowacourse.shopping.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product

@Entity(tableName = "recentlyViewedProduct")
data class RecentlyViewedProduct(
    @PrimaryKey val productId: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String,
    val viewedAt: Long = System.currentTimeMillis(),
)

fun Product.toData(): RecentlyViewedProduct =
    RecentlyViewedProduct(
        productId = this.productId,
        viewedAt = System.currentTimeMillis(),
        name = this.name,
        price = this.price,
        imageUrl = this.imageUrl,
        category = this.category,
    )

fun RecentlyViewedProduct.toDomain(): Product =
    Product(
        productId = this.productId,
        name = this.name,
        _price = Price(this.price),
        imageUrl = this.imageUrl,
        category = this.category,
    )
