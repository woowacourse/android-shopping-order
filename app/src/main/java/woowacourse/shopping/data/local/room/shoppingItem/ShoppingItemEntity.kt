package woowacourse.shopping.data.local.room.shoppingItem

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductTitle
import woowacourse.shopping.domain.model.ShoppingItem

@Entity(tableName = "shopping_items")
data class ShoppingItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "product_id")
    val productId: Long,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "price")
    val price: Int,
    @ColumnInfo(name = "image_url")
    val imageUrl: String,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "quantity")
    val quantity: Int,
)

fun ShoppingItemEntity.toDomain(): ShoppingItem =
    ShoppingItem(
        product =
            Product(
                id = productId,
                title = ProductTitle(title),
                price = Price(price),
                imageUrl = imageUrl,
                category = category,
            ),
        quantity = quantity,
    )
