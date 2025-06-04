package woowacourse.shopping.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import woowacourse.shopping.domain.model.RecentProduct
import java.time.ZoneId

@Entity(tableName = "recent_product")
data class RecentProductEntity(
    @PrimaryKey @ColumnInfo(name = "product_id") val productId: Int,
    @ColumnInfo(name = "viewed_at") val viewedAt: Long = System.currentTimeMillis(),
)

fun RecentProduct.toEntity() =
    RecentProductEntity(
        productId = this.product.id,
        viewedAt = viewedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
    )
