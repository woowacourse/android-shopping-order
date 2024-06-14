package com.example.domain.model

data class CartItem(
    val id: Int = 0,
    val product: Product,
    var quantity: Quantity,
) {
    val price get() = product.price * quantity.count
}
