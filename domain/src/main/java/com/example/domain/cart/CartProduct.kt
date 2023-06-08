package com.example.domain.cart

class CartProduct(
    val id: Long,
    val productId: Long,
    val productImageUrl: String,
    val productName: String,
    val productPrice: Int,
    quantity: Int,
    var isPicked: Boolean = true
) {

    var quantity: Int = quantity
        set(value) {
            field =
                if (value > MAX_COUNT_VALUE) MAX_COUNT_VALUE
                else value.coerceAtLeast(MIN_COUNT_VALUE)
        }

    companion object {
        const val MIN_COUNT_VALUE = 1
        const val MAX_COUNT_VALUE = 99
    }
}
