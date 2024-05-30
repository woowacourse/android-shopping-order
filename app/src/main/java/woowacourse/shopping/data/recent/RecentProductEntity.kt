package woowacourse.shopping.data.recent

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import woowacourse.shopping.domain.RecentProductItem
import java.time.LocalDateTime

@Entity(tableName = "recentProducts", indices = [Index(value = ["product_id"], unique = true)])
data class RecentProductEntity(
    @PrimaryKey(autoGenerate = true) val uid: Long,
    @ColumnInfo(name = "product_id") val productId: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "img_url") val imgUrl: String,
    @ColumnInfo(name = "date_time") val dateTime: LocalDateTime,
    @ColumnInfo(name = "category") val category: String,
) {
    fun toDomain() =
        RecentProductItem(
            productId = productId,
            name = name,
            imgUrl = imgUrl,
            dateTime = dateTime,
            category = category,
        )

    companion object {
        fun RecentProductItem.toEntity() =
            RecentProductEntity(
                uid = 0,
                productId = this.productId,
                name = this.name,
                imgUrl = this.imgUrl,
                dateTime = this.dateTime,
                category = this.category,
            )
    }
}
