package woowacourse.shopping.model.data.dto

import com.google.gson.annotations.SerializedName

data class CartProductDTO(
    @SerializedName("id")
    val id: Long,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("product")
    val product: ProductDTO
)
