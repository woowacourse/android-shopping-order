package woowacourse.shopping.data.local.room.shoppingcart

import androidx.room.ColumnInfo
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductTitle
import woowacourse.shopping.domain.model.ShoppingCartItem
import woowacourse.shopping.domain.model.ShoppingItem

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

fun ShoppingCartItemRow.toDomain(): ShoppingCartItem =
    ShoppingCartItem(
        id = id,
        shoppingItem =
            ShoppingItem(
                product =
                    Product(
                        id = productId,
                        title = ProductTitle(title),
                        price = Price(price),
                        imageUrl = imageUrl,
                    ),
                quantity = quantity,
            ),
    )
