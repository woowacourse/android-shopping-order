package woowacourse.shopping.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recently_viewed_products")
data class RecentlyViewedProductEntity(
    @PrimaryKey @ColumnInfo(name = "product_id") val id: Long,
    @ColumnInfo(name = "time_stamp") val timeStamp: Long = System.currentTimeMillis(),
)
