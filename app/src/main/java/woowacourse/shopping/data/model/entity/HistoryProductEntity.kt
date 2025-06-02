package woowacourse.shopping.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import woowacourse.shopping.domain.model.HistoryProduct

@Entity(tableName = "search_history")
data class HistoryProductEntity(
    @PrimaryKey val productId: Long,
    val name: String,
    val imageUrl: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis(),
) {
    companion object {
        fun HistoryProductEntity.toDomain(): HistoryProduct =
            HistoryProduct(
                productId = productId,
                name = name,
                imageUrl = imageUrl,
                category = category,
            )
    }
}
