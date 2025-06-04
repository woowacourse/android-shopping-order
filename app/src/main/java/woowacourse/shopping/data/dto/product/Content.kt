package woowacourse.shopping.data.dto.product

import com.google.gson.annotations.SerializedName
import woowacourse.shopping.product.catalog.ProductUiModel

data class Content(
    @SerializedName("category")
    val category: String,
    @SerializedName("id")
    val id: Long,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Int,
)

fun Content.toUiModel(): ProductUiModel =
    ProductUiModel(
        id = id,
        imageUrl = imageUrl,
        name = name,
        price = price,
        category = category,
    )
