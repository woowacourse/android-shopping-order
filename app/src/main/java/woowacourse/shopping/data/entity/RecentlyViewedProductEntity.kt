package woowacourse.shopping.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "RecentlyViewedProducts")
class RecentlyViewedProductEntity(
    @PrimaryKey val productUid: Long,
    val timestamp: Long = System.currentTimeMillis(),
)
