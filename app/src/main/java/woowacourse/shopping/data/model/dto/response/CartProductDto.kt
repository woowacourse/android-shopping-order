package woowacourse.shopping.data.model.dto.response

import com.example.domain.model.CartProduct
import com.google.gson.annotations.SerializedName

data class CartProductDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("quantity")
    val count: Int,
    @SerializedName("product")
    val product: ProductDto
) {
    fun toDomain(): CartProduct {
        return CartProduct(id, product.toDomain(), count, true)
    }
}
