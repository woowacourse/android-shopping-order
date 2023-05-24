package woowacourse.shopping.data.model

import com.google.gson.annotations.SerializedName

typealias DataBasketProduct = BasketProduct

data class BasketProduct(
    val id: Int,
    @SerializedName("quantity")
    val count: DataCount,
    val product: DataProduct
)
