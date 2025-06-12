package woowacourse.shopping.data.dto.cartitem

import com.google.gson.annotations.SerializedName
import woowacourse.shopping.product.catalog.ProductUiModel

data class CartItemContent(
    @SerializedName("id")
    val id: Long,
    @SerializedName("product")
    val product: Product,
    @SerializedName("quantity")
    val quantity: Int,
)

fun CartItemContent.toUiModel(): ProductUiModel =
    ProductUiModel(
        id = product.id,
        imageUrl = product.imageUrl,
        name = product.name,
        price = product.price,
        cartItemId = id,
        quantity = quantity,
    )
