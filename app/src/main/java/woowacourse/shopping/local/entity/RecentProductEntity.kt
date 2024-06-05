package woowacourse.shopping.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import woowacourse.shopping.domain.model.Product
import java.time.LocalDateTime

@Entity(tableName = "recentProducts", indices = [Index(value = ["product_id"], unique = true)])
data class RecentProductEntity(
    @PrimaryKey(autoGenerate = true) val uid: Long,
    @ColumnInfo(name = "product_id") val productId: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "img_url") val imgUrl: String,
    @ColumnInfo(name = "date_time") val dateTime: LocalDateTime,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "price") val price: Long = 0L,
) {
    fun toDomain() =
        Product(
            id = productId,
            name = name,
            imgUrl = imgUrl,
            price = price,
            category = category,
        )

    companion object {
        fun Product.toRecentProductEntity(dateTime: LocalDateTime) =
            RecentProductEntity(
                uid = 0,
                productId = this.id,
                name = this.name,
                imgUrl = this.imgUrl,
                dateTime = dateTime,
                price = this.price,
                category = this.category,
            )
    }
}
