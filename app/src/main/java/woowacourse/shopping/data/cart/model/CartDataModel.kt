package woowacourse.shopping.data.cart.model

import com.google.gson.annotations.SerializedName
import woowacourse.shopping.data.product.ProductDataModel

data class CartDataModel(
    val id: Int,
    @SerializedName("product")
    val product: ProductDataModel,
    @SerializedName("quantity")
    var count: Int,
)
