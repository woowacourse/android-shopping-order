package woowacourse.shopping.data.recent.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "recent_products")
class RecentProductEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @Embedded val product: ProductEntity,
    @ColumnInfo(name = "seen_date_time") val seenDateTime: LocalDateTime,
)
