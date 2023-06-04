package woowacourse.shopping.data.dto

import com.example.domain.model.CartProduct
import com.google.gson.annotations.SerializedName
import woowacourse.shopping.mapper.toDomain

data class CartProductDto(
    @SerializedName("id")
    val cartId: Long,

    @SerializedName("product")
    val product: ProductDto,

    @SerializedName("quantity")
    var count: Int,
) {
    fun toDomain() = CartProduct(cartId, product.toDomain(), count, true)
}
