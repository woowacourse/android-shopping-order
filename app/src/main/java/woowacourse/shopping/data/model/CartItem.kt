package woowacourse.shopping.data.model

import com.google.gson.annotations.SerializedName

data class CartItem(
    @SerializedName("id") val cartItemId: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("product") val product: Product,
) {
    val totalPrice: Int
        get() = product.price * quantity

    fun plusQuantity(): CartItem {
        return copy(quantity = quantity + 1)
    }

    fun minusQuantity(): CartItem {
        return copy(quantity = (this.quantity - 1).coerceAtLeast(DEFAULT_QUANTITY))
    }

    companion object {
        const val DEFAULT_QUANTITY = 1
    }
}
