package woowacourse.shopping.data.model

import com.google.gson.annotations.SerializedName
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.repository.CartRepository

data class CartItem2(
    @SerializedName("id") val cartItemId: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("product") val product: Product2,
) {
    fun plusQuantity(): CartItem2 {
        return copy(quantity = quantity + 1)
    }

    fun minusQuantity(): CartItem2 {
        return copy(quantity = (this.quantity - 1).coerceAtLeast(CartRepository.DEFAULT_QUANTITY))
    }

    fun totalPrice(): Int = product.price * quantity
}
