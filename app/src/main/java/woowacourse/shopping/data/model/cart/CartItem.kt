package woowacourse.shopping.data.model.cart

import com.google.gson.annotations.SerializedName
import woowacourse.shopping.data.model.product.Product

data class CartItem(
    @SerializedName("id") val cartItemId: Int,
    val quantity: Int,
    val product: Product,
)
