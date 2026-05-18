package woowacourse.shopping.local.recent

import androidx.room.Entity
import androidx.room.PrimaryKey
import woowacourse.shopping.model.RecentProduct

@Entity(tableName = "recent_products")
data class RecentProductEntity(
    @PrimaryKey val productId: Long,
    val viewedAtMillis: Long,
) {
    fun toDomain(): RecentProduct =
        RecentProduct(
            productId = (productId),
            viewedAtMillis = viewedAtMillis,
        )

    companion object {
        fun fromDomain(recentProduct: RecentProduct): RecentProductEntity =
            RecentProductEntity(
                productId = recentProduct.productId,
                viewedAtMillis = recentProduct.viewedAtMillis,
            )
    }
}
