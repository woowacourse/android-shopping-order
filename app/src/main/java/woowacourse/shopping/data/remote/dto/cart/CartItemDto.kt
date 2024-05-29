package woowacourse.shopping.data.remote.dto.cart


import com.google.gson.annotations.SerializedName
import woowacourse.shopping.data.remote.dto.product.ProductDto

data class CartItemDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("product")
    val product: ProductDto,
    @SerializedName("quantity")
    val quantity: Int
)
