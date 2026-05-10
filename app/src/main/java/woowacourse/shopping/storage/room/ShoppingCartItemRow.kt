package woowacourse.shopping.storage.room

import androidx.room.ColumnInfo

data class ShoppingCartItemRow(
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "product_id")
    val productId: Long,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "price")
    val price: Int,
    @ColumnInfo(name = "image_url")
    val imageUrl: String,
    @ColumnInfo(name = "quantity")
    val quantity: Int,
)

