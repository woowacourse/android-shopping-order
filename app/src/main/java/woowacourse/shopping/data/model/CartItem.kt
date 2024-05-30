package woowacourse.shopping.data.model

import com.google.gson.annotations.SerializedName

data class CartItem(
    @SerializedName("id") val cartItemId: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("product") val product: Product,
) {
    fun plusQuantity(): CartItem {
        return copy(quantity = quantity + 1)
    }

    fun minusQuantity(): CartItem {
        return copy(quantity = (this.quantity - 1).coerceAtLeast(DEFAULT_QUANTITY))
    }

    fun totalPrice(): Int = product.price * quantity

    companion object {
        const val DEFAULT_QUANTITY = 1
    }
}
