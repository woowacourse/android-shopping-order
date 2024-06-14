package woowacourse.shopping.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import woowacourse.shopping.domain.model.Product

@Entity(tableName = "recent_products")
data class RecentProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val productId: Long,
    val productName: String,
    val imgUrl: String,
    val category: String,
)

fun Product.mapper(): RecentProductEntity {
    return RecentProductEntity(
        productId = this.id,
        productName = this.name,
        imgUrl = this.imageUrl,
        category = this.category,
    )
}
