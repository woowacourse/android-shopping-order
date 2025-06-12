package woowacourse.shopping.data.local.history

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import woowacourse.shopping.domain.model.Product

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "price") val price: Int,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis(),
) {
    fun toDomain(): Product =
        Product(
            id = id,
            name = name,
            price = price,
            imageUrl = imageUrl,
            category = category,
        )
}

fun Product.toHistoryEntity(): HistoryEntity =
    HistoryEntity(
        id = id,
        name = name,
        price = price,
        imageUrl = imageUrl,
        category = category ?: "",
    )
