package woowacourse.shopping.data.model

import com.google.gson.annotations.SerializedName

data class BasketProductEntity(
    val id: Int,
    @SerializedName("quantity")
    val count: Int,
    val product: ProductEntity,
)
