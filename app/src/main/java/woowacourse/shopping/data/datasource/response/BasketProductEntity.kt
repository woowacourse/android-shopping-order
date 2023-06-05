package woowacourse.shopping.data.datasource.response

import com.google.gson.annotations.SerializedName

data class BasketProductEntity(
    @SerializedName("id")
    val id: Int,
    @SerializedName("quantity")
    val count: Int,
    @SerializedName("product")
    val product: ProductEntity,
)
