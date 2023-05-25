package woowacourse.shopping.data.cart

import com.google.gson.annotations.SerializedName
import woowacourse.shopping.data.product.ProductDataModel

data class CartRemoteDataModel(
    val id: Int,
    @SerializedName("product")
    val product: ProductDataModel,
    @SerializedName("quantity")
    var count: Int
)
